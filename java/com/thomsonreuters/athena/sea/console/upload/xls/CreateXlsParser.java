package com.thomsonreuters.athena.sea.console.upload.xls;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.thomsonreuters.athena.sea.console.upload.entity.CreateXlsUserEntity;

/**
 * @author Miro Zorboski
 * 
 */
public class CreateXlsParser extends AbstractXlsParser<CreateXlsUserEntity> {
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.xls.AbstractXlsParser#getXlsEntity(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	protected CreateXlsUserEntity getXlsEntity(HSSFRow userRow) throws Exception {
		CreateXlsUserEntity xlsUser = new CreateXlsUserEntity();

		int c = 0;

		xlsUser.setXlsRowNumber(userRow.getRowNum() + 1);
		xlsUser.setSubscriberCode(getCellValue(userRow, c++).toUpperCase());
		xlsUser.setSubAccount(getCellValue(userRow, c++));
		xlsUser.setCoordinator(getCellValue(userRow, c++));
		xlsUser.setOrderNumber(getCellValue(userRow, c++));
		xlsUser.setEmail(getCellValue(userRow, c++));
		xlsUser.setFirstName(getCellValue(userRow, c++));
		xlsUser.setLastName(getCellValue(userRow, c++));
		xlsUser.setUserLogin(getCellValue(userRow, c++));
		xlsUser.setPassword(getCellValue(userRow, c++));
		xlsUser.setAthenaLogin(getCellValue(userRow, c++));
		xlsUser.setAthenaPassword(getCellValue(userRow, c++));
		xlsUser.setServer(getCellValue(userRow, c++));
		xlsUser.setSeedAccount(getCellValue(userRow, c++));
		xlsUser.setAkr(getCellValue(userRow, c++));
		xlsUser.setProducts(parseProducts(getCellValue(userRow, c++)));
		xlsUser.setComment(getCellValue(userRow, c++));
		xlsUser.setBillingNote(getCellValue(userRow, c++));
		
		return xlsUser;
    }
	
	/**
	 * Parses the products.
	 * 
	 * @param productsString the products string
	 * 
	 * @return the list< string>
	 */
	@Deprecated
	private List<String> parseProducts(String productsString) {
		List<String> products = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(productsString, ",");
		while(stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken().trim();
			if(!products.contains(token)) {
				products.add(token);
			}
		}
		
		return products;
	}
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.xls.AbstractXlsParser#mergedEntities(com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity, com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity)
	 */
	@Override
	protected boolean mergedEntities(CreateXlsUserEntity mainEntity,
			CreateXlsUserEntity newEntity) throws Exception {
		
		if(mainEntity != null) {
			if(newEntity.getSubscriberCode().isEmpty()
					&& newEntity.getUserLogin().isEmpty()
					&& !newEntity.getProducts().isEmpty()) {
				
				for(String prod : newEntity.getProducts()) {
					if(!mainEntity.getProducts().contains(prod.trim())) {
						mainEntity.getProducts().add(prod);
					}	
				}
				
				return true;
			}
		}
		
		return false;
	}
	
}
