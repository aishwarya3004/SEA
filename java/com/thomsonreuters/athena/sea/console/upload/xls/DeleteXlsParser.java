package com.thomsonreuters.athena.sea.console.upload.xls;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.thomsonreuters.athena.sea.console.upload.entity.DeleteXlsUserEntity;

/**
 * @author Miro Zorboski
 * 
 */
public class DeleteXlsParser extends AbstractXlsParser<DeleteXlsUserEntity> {

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.xls.AbstractXlsParser#getXlsEntity(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	@Override
	protected DeleteXlsUserEntity getXlsEntity(HSSFRow userRow) throws Exception {
		DeleteXlsUserEntity deleteEntity = new DeleteXlsUserEntity();
		
		int c = 0;
		
		deleteEntity.setXlsRowNumber(userRow.getRowNum() + 1);
		deleteEntity.setSystem(getCellValue(userRow, c++));
		deleteEntity.setSubscriberCode(getCellValue(userRow, c++).toUpperCase());
		deleteEntity.setSubAccount(getCellValue(userRow, c++));
		deleteEntity.setUserId(getCellValue(userRow, c++));
		deleteEntity.setComment(getCellValue(userRow, c++));

		return deleteEntity;
	}

}
