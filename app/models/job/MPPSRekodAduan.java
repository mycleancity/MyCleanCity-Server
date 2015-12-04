package models.job;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import models.Constant;
import models.db.Complaint;
import models.db.SOAP;

import org.apache.commons.lang.StringEscapeUtils;

import play.Logger;
import play.Play;
import play.jobs.Job;

public class MPPSRekodAduan extends Job {

	public final Long id;

	public MPPSRekodAduan(Long id) {
		this.id = id;
	}

	public void doJob() {
		if (!Play.id.equalsIgnoreCase("prod"))
			return;
		SOAP soap = null;
		try {
			Complaint complaint = Complaint.findById(id);
			if (complaint != null) {

				String keterangan = StringEscapeUtils
						.escapeXml(complaint.story)
						+ " [Date: "
						+ complaint.log.createdAt
						+ "] \n\r [Submission Date: "
						+ new Date()
						+ "] \n\r [Tel: "
						+ complaint.contactNo
						+ "] \n\r [Coordinate: "
						+ complaint.location.getX()
						+ ", "
						+ complaint.location.getY()
						+ "] \n\r [Address: "
						+ complaint.address
						+ "] \n\r [Photo: http://mycleancity.my"
						+ complaint.photo.url("O") + "]";

				Logger.info("~~~ MPPSRekodAduan Start: %s ~~~~", complaint.id);
				MessageFactory messageFactory = MessageFactory.newInstance();
				SOAPMessage soapMessage = messageFactory.createMessage();
				SOAPPart soapPart = soapMessage.getSOAPPart();

				SOAPEnvelope envelope = soapPart.getEnvelope();
				SOAPBody soapBody = envelope.getBody();
				SOAPElement element = soapBody.addChildElement("RekodAduan",
						"", Constant.MPPJ_SOAP_URL);
				element.addChildElement("USER_ID_PENGGUNA").addTextNode(
						complaint.log.createdBy.id.toString());
				element.addChildElement("PRIORITI").addTextNode("Kategori 1");
				element.addChildElement("SUMBER").addTextNode("C4");

				element.addChildElement("ID_KATEGORI").addTextNode(
						complaint.category.mbpjID);
				element.addChildElement("KETERANGAN_KATEGORI").addTextNode(
						complaint.category.mbpjName);

				element.addChildElement("KETERANGAN_ADUAN").addTextNode(
						keterangan);
				element.addChildElement("TKH_ADUAN").addTextNode(
						new SimpleDateFormat("dd-MMM-yyyy")
								.format(complaint.log.createdAt));
				element.addChildElement("ID_ZON").addTextNode(
						complaint.zone.zid);
				// element.addChildElement("ID_KAWASAN").addTextNode("1");
				// element.addChildElement("ID_JALAN").addTextNode("1");
				element.addChildElement("NAMA_PENGADU").addTextNode(
						complaint.log.createdBy.name);
				element.addChildElement("EMEL").addTextNode(
						complaint.log.createdBy.email);

				// element.addChildElement("ALAMAT").addTextNode("ABC");
				// element.addChildElement("BANDAR").addTextNode("ABC");
				// element.addChildElement("NEGERI").addTextNode("ABC");
				// element.addChildElement("POSKOD").addTextNode("ABC");
				// element.addChildElement("NOTEL_R").addTextNode("ABC");
				// element.addChildElement("NOTEL_P").addTextNode("ABC");
				// element.addChildElement("NOTEL_HP").addTextNode("ABC");

				MimeHeaders headers = soapMessage.getMimeHeaders();
				headers.addHeader("SOAPAction", Constant.MPPJ_SOAP_URL
						+ "RekodAduan");
				soapMessage.saveChanges();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				soapMessage.writeTo(out);
				String request = new String(out.toByteArray());
				soap = SOAP.create(complaint, SOAP.REKOD_ADUAN, request);

				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
						.newInstance();
				SOAPConnection soapConnection = soapConnectionFactory
						.createConnection();

				SOAPMessage soapResponse = soapConnection.call(soapMessage,
						Constant.MPPJ_SOAP_API);

				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				Source sourceContent = soapResponse.getSOAPPart().getContent();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");
				StringWriter writer = new StringWriter();
				transformer.transform(sourceContent, new StreamResult(writer));
				String response = writer.getBuffer().toString();
				soap.update(true, response);

				String complaintID = soapResponse.getSOAPPart().getEnvelope()
						.getTextContent().replaceAll("\\<[^>]*>", "");

				if (complaintID.length() < 15) {
					complaint.complaintID = complaintID;
					complaint.mppjStatus = Constant.MPPJ_BELUM_SAH;
					complaint.save();
				} else {
					complaint.invalid(complaintID, null);
				}

				Logger.info("~~~ MPPSRekodAduan End: %s ~~~~", complaintID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (soap != null) {
				soap.update(false, e.getMessage());
			}
		}
	}

}
