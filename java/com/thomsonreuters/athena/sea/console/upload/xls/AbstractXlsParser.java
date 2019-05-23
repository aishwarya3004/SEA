package com.thomsonreuters.athena.sea.console.upload.xls;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity;
import com.thomsonreuters.athena.sea.console.upload.xls.util.SEAConsoleException;

/**
 * @author Miro Zorboski
 * 
 */
public abstract class AbstractXlsParser<T extends AbstractXlsEntity> {

	/**
	 * Parses the.
	 * 
	 * @param xlsData the xls data
	 * 
	 * @return the list< t>
	 * 
	 * @throws Exception the exception
	 */
	public List<T> parse(byte[] xlsData) throws Exception {
		List<T> xlsEntities = new ArrayList<T>(); 
		
		try {
			ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(xlsData);
			HSSFWorkbook workbook = new HSSFWorkbook(byteArrayIn); 
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> rows = sheet.rowIterator();
			//Skip header
			rows.next();
			
			T currentXlsEntity = null;
			
            while(rows.hasNext()) {
                HSSFRow userRow = (HSSFRow) rows.next();
                
                T newXlsEntity;
                if(userRow.getPhysicalNumberOfCells() < 1) {
                	continue;
                } else {
                	newXlsEntity = getXlsEntity(userRow);
                }
 
                
                //Merge new entity with old one if needed
                if(!mergedEntities(currentXlsEntity, newXlsEntity)) {
                	xlsEntities.add(newXlsEntity);
                	currentXlsEntity = newXlsEntity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(!(e instanceof SEAConsoleException)) {
            	throw new HPSFException("Wrong xls file structure");
            } else {
            	throw e;
            }
        }
        
        return xlsEntities;
	}
	
	/**
	 * Gets the xls entity.
	 * 
	 * @param userRow the user row
	 * 
	 * @return the xls entity
	 * 
	 * @throws Exception the exception
	 */
	protected abstract T getXlsEntity(HSSFRow userRow) throws Exception;
	
	/**
	 * Merged entities.
	 * 
	 * @param mainEntity the main entity
	 * @param newEntity the new entity
	 * 
	 * @return the t
	 * 
	 * @throws Exception the exception
	 */
	protected boolean mergedEntities(T mainEntity, T newEntity) throws Exception {
		return false;
	}
	
	/**
	 * Gets the cell value.
	 * 
	 * @param row the row
	 * @param index the index
	 * 
	 * @return the cell value
	 */
	protected String getCellValue(HSSFRow row, Integer index) {
		if(row.getCell(index) == null) {
			return "";
		} else if(row.getCell(index).getCellType() == Cell.CELL_TYPE_BLANK) {
			return "";
		} else if(row.getCell(index).getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return getCellIntegerByIndex(row, index).toString();
		} else {
			return getCellStringByIndex(row, index);
		}
	}
	
	/**
	 * Gets the cell string by index.
	 * 
	 * @param row the row
	 * @param index the index
	 * 
	 * @return the cell string by index
	 */
	protected String getCellStringByIndex(HSSFRow row, Integer index) {
		return row.getCell(index) != null ? row.getCell(index).getRichStringCellValue().toString().trim() : null;
	}

	/**
	 * Gets the cell integer by index.
	 * 
	 * @param row the row
	 * @param index the index
	 * 
	 * @return the cell integer by index
	 */
	protected Integer getCellIntegerByIndex(HSSFRow row, Integer index) {
		return row.getCell(index) != null ? (int)row.getCell(index).getNumericCellValue() : null;
	}
	
}
