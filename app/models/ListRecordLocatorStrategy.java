package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import play.Logger;
import play.modules.paginate.strategy.ByValueRecordLocatorStrategy;

public abstract class ListRecordLocatorStrategy<T> extends
		ByValueRecordLocatorStrategy<T> {

	private final List<T> values;

	public ListRecordLocatorStrategy() {
		values = new ArrayList<T>();
	}

	public ListRecordLocatorStrategy(List<T> values) {
		this.values = values;
	}

	public ListRecordLocatorStrategy(Collection<T> values) {
		this.values = new ArrayList(values);
	}

	@Override
	public List<T> fetchPage(int startRowIdx, int lastRowIdx) {
		if (values == null || values.size() == 0 || startRowIdx > lastRowIdx)
			return Collections.emptyList();
//		Logger.info("startRowIdx: " + startRowIdx);
//		Logger.info("lastRowIdx: " + lastRowIdx);

		int pageSize = lastRowIdx - startRowIdx;
		int page = lastRowIdx / pageSize;
		if (page > 1) {
			lastRowIdx = lastRowIdx - startRowIdx;
			startRowIdx = 0;
//			startRowIdx = startRowIdx - (pageSize * (page - 1));
//			lastRowIdx = lastRowIdx - (pageSize * (page - 1));
		}

//		Logger.info("page: " + page);
//		Logger.info("pageSize: " + pageSize);
//		Logger.info("startRowIdx: " + startRowIdx);
//		Logger.info("lastRowIdx: " + lastRowIdx);
		List<T> pageValues = values.subList(startRowIdx, lastRowIdx);
		return pageValues;
	}

	@Override
	public int indexOf(T t) {
		return -1;
	}

	@Override
	public int lastIndexOf(T t) {
		return -1;
	}

	@Override
	public int count() {
		return max();
	}

	public abstract int max();
}
