package com.thomsonreuters.athena.sea.console.upload.xls;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.thomsonreuters.athena.sea.console.upload.entity.ModifyXlsUserEntity;
import com.thomsonreuters.athena.sea.console.upload.xls.util.SEAConsoleException;
import com.thomsonreuters.athena.sea.entity.TransactionProduct.ProductAction;

/**
 * @author Miro Zorboski
 * 
 */
public class ModifyXlsParser extends AbstractXlsParser<ModifyXlsUserEntity> {

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.xls.AbstractXlsParser#getXlsEntity(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	@Override
	protected ModifyXlsUserEntity getXlsEntity(HSSFRow userRow) throws Exception {
		ModifyXlsUserEntity modifyEntity = new ModifyXlsUserEntity();
		
		int c = 0;
		
		modifyEntity.setXlsRowNumber(userRow.getRowNum() + 1);
		modifyEntity.setSystem(getCellValue(userRow, c++));
		modifyEntity.setSubscriberCode(getCellValue(userRow, c++).toUpperCase());
		modifyEntity.setSubAccount(getCellValue(userRow, c++));
		modifyEntity.setUserId(getCellValue(userRow, c++));
		
		//Product
		List<String> products = new ArrayList<String>();
		String product = getCellValue(userRow, c++);
		products.add(product);
		String productAction = getCellValue(userRow, c++);
	
		if(productAction.equals(ProductAction.Install.getCode())) {
			modifyEntity.setProductsToAdd(products);
		} else if (productAction.equals(ProductAction.Remove.getCode())) {
			modifyEntity.setProductsToRemove(products);
		} else {
			throw new SEAConsoleException("Invalid product action in row: " + modifyEntity.getXlsRowNumber());
		}
		
		modifyEntity.setComment(getCellValue(userRow, c++));
		modifyEntity.setBillingNote(getCellValue(userRow, c++));

		return modifyEntity;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.athena.sea.console.upload.xls.AbstractXlsParser#mergedEntities(com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity, com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity)
	 */
	@Override
	protected boolean mergedEntities(ModifyXlsUserEntity mainEntity,
			ModifyXlsUserEntity newEntity) throws Exception {
		
		if(mainEntity != null) {
			if(newEntity.getSubscriberCode().isEmpty()
					&& newEntity.getUserId().isEmpty()
					&& !newEntity.getProductsToAdd().isEmpty()) {
				
				for(String prod : newEntity.getProductsToAdd()) {
					if(!mainEntity.getProductsToAdd().contains(prod.trim())) {
						mainEntity.getProductsToAdd().add(prod);
					}	
				}
				
				return true;
			} else if(newEntity.getSubscriberCode().isEmpty()
					&& newEntity.getUserId().isEmpty()
					&& !newEntity.getProductsToRemove().isEmpty()) {
				
				for(String prod : newEntity.getProductsToRemove()) {
					if(!mainEntity.getProductsToRemove().contains(prod.trim())) {
						mainEntity.getProductsToRemove().add(prod);
					}	
				}
				
				return true;
			}
		}
		
		return false;
	}

}
