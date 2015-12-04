package models.job;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;

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

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.db.jpa.GenericModel.JPAQuery;
import play.jobs.Every;
import play.jobs.Job;

@Every("20mn")
public class MPPSMaklumbalasAduan extends Job {

	public void doJob() throws Exception {
		if (!Play.id.equalsIgnoreCase("prod"))
			return;
		Logger.info("----------- MPPSMaklumbalasAduan Start -----------");
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Complaint c WHERE c.complaintID IS NOT NULL ");
		sql.append("AND c.mppjValidated IS TRUE ");
		sql.append("AND c.mppjFinished IS FALSE ");
		sql.append("ORDER BY c.mppjCounter ASC ");

		JPAQuery query = Complaint.find(sql.toString());
		List<Complaint> complaints = query.fetch(10);
		Logger.info("Found Complaints: %s", complaints.size());
		for (Complaint complaint : complaints) {
			complaint.mppjCounter++;
			complaint.save();
			check(complaint);
			Logger.info("Check Complaint: %s", complaint.complaintID);
		}
		Logger.info("----------- MPPSMaklumbalasAduan End -----------");
	}

	private void check(Complaint complaint) {
		SOAP soap = null;
		try {
			if (complaint != null
					&& StringUtils.isNotEmpty(complaint.complaintID)) {
				MessageFactory messageFactory = MessageFactory.newInstance();
				SOAPMessage soapMessage = messageFactory.createMessage();
				SOAPPart soapPart = soapMessage.getSOAPPart();

				SOAPEnvelope envelope = soapPart.getEnvelope();
				SOAPBody soapBody = envelope.getBody();
				SOAPElement element = soapBody.addChildElement(
						"MaklumbalasAduan", "", Constant.MPPJ_SOAP_URL);
				element.addChildElement("NOMBOR_ADUAN").addTextNode(
						complaint.complaintID);
				MimeHeaders headers = soapMessage.getMimeHeaders();
				headers.addHeader("SOAPAction", Constant.MPPJ_SOAP_URL
						+ "MaklumbalasAduan");
				soapMessage.saveChanges();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				soapMessage.writeTo(out);
				String request = new String(out.toByteArray());
				soap = SOAP.create(complaint, SOAP.MAKLUM_BALAS_ADUAN, request);

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

				String status = soapResponse.getSOAPPart().getEnvelope()
						.getTextContent().replaceAll("\\<[^>]*>", "");
				Logger.info("RESULT: %s", status);
				if (status.startsWith("soap:ServerServer"))
					return;

				complaint.mppjStatus = status;
				complaint.save();

				if (complaint.mppjStatus
						.equalsIgnoreCase(Constant.MPPJ_SELESAI)) {
					complaint.mppjFinished = true;
					complaint.mppjCounter = 0;
					complaint.resolve(null);
				} else if (complaint.mppjStatus
						.equalsIgnoreCase(Constant.MPPJ_SEDANG_TINDAKAN)) {
					if (complaint.status != Constant.SLA_IN_PROGRESS) {
						complaint.mppjCounter = 0;
						complaint.proceed(complaint.category.slaDays, null);
					}
				} else if (complaint.mppjStatus
						.equalsIgnoreCase(Constant.MPPJ_TIDAK_SAH)
						|| complaint.mppjStatus
								.equalsIgnoreCase(Constant.MPPJ_TIADA_REKOD)
						|| complaint.mppjStatus
								.equalsIgnoreCase(Constant.MPPJ_BERULANG)
						|| complaint.mppjStatus
								.equalsIgnoreCase(Constant.MPPJ_PALSU)) {
					complaint.mppjValidated = true;
					complaint.mppjFinished = true;
					complaint.mppjCounter = 0;
					complaint.invalid(complaint.mppjStatus, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (soap != null) {
				soap.update(false, e.getMessage());
			}
		}
	}
}
