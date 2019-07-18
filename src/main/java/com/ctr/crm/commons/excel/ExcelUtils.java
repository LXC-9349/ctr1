package com.ctr.crm.commons.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletResponse;

import com.ctr.crm.moduls.member.models.MemberInfo;
import com.ctr.crm.moduls.sysdict.models.SysDict;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;

import com.ctr.crm.commons.utils.AnnontationUtils;
import com.ctr.crm.commons.utils.CommonUtils;

public class ExcelUtils {
	private static final Log log = LogFactory.getLog("exception_excelimport");
	// 前台导出excel定位字符串
	public static final String EXPORTPARAM = "excel123542785436520=1";
	public static final String EXPORTALLPARAM = "exportall=1";
	public static final int sbi = 'A' - 'a';
	public static final String SEPARATOR = ",";

	/**
	 * 
	 * 方法说明：取得map中的值或bean对象中的值(一定要符合bean规则)
	 * 
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getObjValue(Object item, String field) throws Exception {
		if (item instanceof Map) {
			return ((Map) item).get(field);
		} else {
			Class c = item.getClass();
			char firstLetter = field.charAt(0);
			if ('a' <= firstLetter && firstLetter <= 'z') {
				field = ((char) (firstLetter + ExcelUtils.sbi)) + field.substring(1);
			}
			String methodName = "get" + field;
			Method m = c.getDeclaredMethod(methodName);
			return m.invoke(item);
		}
	}

	public static HSSFWorkbook generateExcel(List<? extends Object> dataList, List<ExcelDecoratedEntry> params) {
		return generateExcel(dataList, params, null);
	}

	public static HSSFWorkbook generateExcel(List<? extends Object> dataList, List<ExcelDecoratedEntry> params,
			List<int[]> mergeList) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		HSSFWorkbook wb = generateHead(params, mergeList);
		try {
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			// 生成内容
			int cIndex = sheet.getLastRowNum() + 1;
			if (null != dataList && !dataList.isEmpty()) {
				for (int i = 0; i < dataList.size(); i++) {
					Object item = dataList.get(i);
					HSSFRow bodyRow = sheet.createRow(cIndex++);
					int columnIndex = 0;
					for (int j = 0; j < params.size(); j++) {
						ExcelDecoratedEntry param = params.get(j);
						String key = param.getKey();
						if (StringUtils.isEmpty(key))
							continue;
						Object value = getObjValue(item, key);
						if (value == null)
							value = "";
						HSSFCell cell = bodyRow.createCell(columnIndex++);
						cell.setCellStyle(cellStyle);
						if (value instanceof Number) {
							if (StringUtils.isNotBlank(param.getFormatPattern())) {
								NumberFormat nf = new DecimalFormat(param.getFormatPattern());
								value = nf.format(value);
								cell.setCellValue(new HSSFRichTextString(value.toString()));
							} else {
								cell.setCellValue(((Number) value).doubleValue());
							}
						} else if (value instanceof Date) {
							if (StringUtils.isNotBlank(param.getFormatPattern())) {
								cell.setCellValue(new HSSFRichTextString(
										CommonUtils.formateDateToStr((Date) value, param.getFormatPattern())));
							} else {
								cell.setCellValue(new HSSFRichTextString(CommonUtils.formateDateToStr((Date) value)));
							}
						} else if (value instanceof Calendar) {
							cell.setCellValue((Calendar) value);
						} else if (value instanceof Boolean) {
							String temp = (Boolean) value ? "是" : "否";
							cell.setCellValue(new HSSFRichTextString(temp));
						} else {
							cell.setCellValue(new HSSFRichTextString(value.toString()));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("生成excel文件出错");
		}
		return wb;
	}

	public static HSSFWorkbook generateHead(List<ExcelDecoratedEntry> params, List<int[]> mergeList) {
		if (params == null || params.isEmpty()) {
			return null;
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet");
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 标题垂直居中
		headerStyle.setAlignment(HorizontalAlignment.CENTER);// 标题水平居中
		headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
		headerStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());
		HSSFFont bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		headerStyle.setFont(bold);
		if (mergeList != null && mergeList.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				ExcelDecoratedEntry param = params.get(i);
				String title = param.getTitle();
				int rowIndex = param.getRowIndex();
				int columnIndex = param.getColumnIndex();
				HSSFRow titleRow = sheet.getRow(rowIndex);
				if (titleRow == null) {
					titleRow = sheet.createRow(rowIndex);
				}
				HSSFCell cell = titleRow.createCell(columnIndex);
				cell.setCellValue(new HSSFRichTextString(title));
				cell.setCellStyle(headerStyle);
				sheet.setColumnWidth(columnIndex, param.getWidth());
			}
			// 合并单元格
			for (int i = 0; i < mergeList.size(); i++) {
				int[] indexArray = mergeList.get(i);
				sheet.addMergedRegion(new CellRangeAddress(indexArray[0], indexArray[1], indexArray[2], indexArray[3]));
			}
		} else {
			HSSFRow titleRow = sheet.createRow(0);
			for (int i = 0; i < params.size(); i++) {
				ExcelDecoratedEntry param = params.get(i);
				String title = param.getTitle();
				HSSFCell cell = titleRow.createCell(i);
				cell.setCellValue(new HSSFRichTextString(title));
				cell.setCellStyle(headerStyle);
				sheet.setColumnWidth(i, param.getWidth());
			}
		}

		return wb;
	}

	public static String[] getDataFromExcel(File file) {
		String[] datas = null;
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			datas = new String[rows];
			for (int i = 0; i < rows; i++) {
				HSSFRow row = sheet.getRow(i);
				if (null != row) {
					int cells = row.getPhysicalNumberOfCells();
					StringBuffer value = new StringBuffer("");
					for (int j = 0; j < cells; j++) {
						HSSFCell cell = row.getCell(j);
						if (cell != null) {
							String val = "";
							switch (cell.getCellTypeEnum()) {
							case FORMULA:
								break;
							case NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
									val = CommonUtils.formateDateToStr(date, "yyyy-MM-dd HH:mm:ss");
								} else {
									val = String.valueOf((long) cell.getNumericCellValue());
								}
								break;
							case STRING:
								val = String.valueOf(cell.getRichStringCellValue().getString());
								break;
							case BOOLEAN:
								val = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								val = "";
								break;
							}
							if (value.toString().equals("")) {
								value.append(val);
							} else {
								value.append(SEPARATOR).append(val);
							}

						}
					}
					datas[i] = value.toString();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("解析excel文件出错");
		} finally{
			try {
				if(workbook != null)
					workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return datas;
	}

	public static void generateHead(HSSFWorkbook workbook, int sheetNum, String sheetTitle,
			List<ExcelDecoratedEntry> params, List<int[]> mergeList) {
		if (workbook == null || params == null || params.isEmpty()) {
			return;
		}
		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(sheetNum, sheetTitle);
		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 标题垂直居中
		headerStyle.setAlignment(HorizontalAlignment.CENTER);// 标题水平居中
		headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
		headerStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());
		HSSFFont bold = workbook.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		headerStyle.setFont(bold);
		if (mergeList != null && mergeList.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				ExcelDecoratedEntry param = params.get(i);
				String title = param.getTitle();
				int rowIndex = param.getRowIndex();
				int columnIndex = param.getColumnIndex();
				HSSFRow titleRow = sheet.getRow(rowIndex);
				if (titleRow == null) {
					titleRow = sheet.createRow(rowIndex);
				}
				HSSFCell cell = titleRow.createCell(columnIndex);
				cell.setCellValue(new HSSFRichTextString(title));
				cell.setCellStyle(headerStyle);
				sheet.setColumnWidth(columnIndex, param.getWidth());
			}
			// 合并单元格
			for (int i = 0; i < mergeList.size(); i++) {
				int[] indexArray = mergeList.get(i);
				sheet.addMergedRegion(new CellRangeAddress(indexArray[0], indexArray[1], indexArray[2], indexArray[3]));
			}
		} else {
			HSSFRow titleRow = sheet.createRow(0);
			for (int i = 0; i < params.size(); i++) {
				ExcelDecoratedEntry param = params.get(i);
				String title = param.getTitle();
				HSSFCell cell = titleRow.createCell(i);
				cell.setCellValue(new HSSFRichTextString(title));
				cell.setCellStyle(headerStyle);
				sheet.setColumnWidth(i, param.getWidth());
			}
		}
	}

	public static void exportExcel(HSSFWorkbook workbook, int sheetNum, String sheetTitle,
			List<ExcelDecoratedEntry> params, List<int[]> mergeList, List<? extends Object> dataList) throws Exception {
		if (workbook == null)
			return;
		generateHead(workbook, sheetNum, sheetTitle, params, mergeList);
		HSSFSheet sheet = workbook.getSheetAt(sheetNum);
		if (dataList != null) {
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			// 生成内容
			int cIndex = sheet.getLastRowNum() + 1;
			for (int i = 0; i < dataList.size(); i++) {
				Object item = dataList.get(i);
				HSSFRow bodyRow = sheet.createRow(cIndex++);
				int columnIndex = 0;
				for (int j = 0; j < params.size(); j++) {
					ExcelDecoratedEntry param = params.get(j);
					String key = param.getKey();
					if (StringUtils.isEmpty(key))
						continue;
					Object value = getObjValue(item, key);
					if (value == null)
						value = "";
					HSSFCell cell = bodyRow.createCell(columnIndex++);
					cell.setCellStyle(cellStyle);
					if (value instanceof Number) {
						if (StringUtils.isNotBlank(param.getFormatPattern())) {
							NumberFormat nf = new DecimalFormat(param.getFormatPattern());
							value = nf.format(value);
							cell.setCellValue(new HSSFRichTextString(value.toString()));
						} else {
							cell.setCellValue(((Number) value).doubleValue());
						}
					} else if (value instanceof Date) {
						if (StringUtils.isNotBlank(param.getFormatPattern())) {
							cell.setCellValue(new HSSFRichTextString(
									CommonUtils.formateDateToStr((Date) value, param.getFormatPattern())));
						} else {
							cell.setCellValue(new HSSFRichTextString(CommonUtils.formateDateToStr((Date) value)));
						}
					} else if (value instanceof Calendar) {
						cell.setCellValue((Calendar) value);
					} else if (value instanceof Boolean) {
						String temp = (Boolean) value ? "是" : "否";
						cell.setCellValue(new HSSFRichTextString(temp));
					} else {
						cell.setCellValue(new HSSFRichTextString(value.toString()));
					}
				}
			}
		}
	}

	// 解析excel
	public static List<List<String>> getListFromExcel(MultipartFile fileExcel, String fileExcelFileNme)
			throws Exception {
		if (null == fileExcel || StringUtils.isBlank(fileExcelFileNme))
			return null;
		boolean is03excel = fileExcelFileNme.matches("^.+\\.(?i)(xls)$"); // 判断是否是03版
		InputStream inputStream = fileExcel.getInputStream();
		List<List<String>> resultList = new ArrayList<List<String>>();
		try{
			Workbook workbook = (Workbook) (is03excel ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream));
			Sheet sheet = workbook.getSheetAt(0);// 获取首个Sheet表
			if (null == sheet)
				return null;
			int physicalNumberOfRows = sheet.getPhysicalNumberOfRows(); // 获取行数
			if (physicalNumberOfRows <= 1) // 行数大于1(默认从第二行读取)
				return null;
			int physicalNumberOfCells = sheet.getRow(0).getPhysicalNumberOfCells();// 获取表头总列数
			if (physicalNumberOfCells <= 0)
				return null;
	
			SimpleDateFormat formater = new SimpleDateFormat();
			formater.applyPattern("yyyy-MM-dd HH:mm:ss");
	
			Row row = null; // 获取单行
			List<String> rowList = null;// 转换后的行数据
			int k = 1;
			try {
				for (; k < physicalNumberOfRows; k++) {
					row = sheet.getRow(k);
					if (null == row)
						continue;
					rowList = new ArrayList<String>();
					for (int i = 0; i < physicalNumberOfCells; i++) {
						Cell cell = row.getCell(i); // 读取单个单元格
						if (null == cell) {
							rowList.add("");
							continue;
						}
						rowList.add(getStringVal(cell, formater));
					}
					// 保存用户到集合
					resultList.add(rowList);
				}
			} catch (Exception e) {
				log.error("excel import error from row: " + (k + 1), e);
			}
		}catch(Exception e){
			log.error("getListFromExcel error.", e);
		}finally{
			if(inputStream != null){
				inputStream.close();
			}
		}
		return resultList;
	}

	public static String getStringVal(Cell cell, SimpleDateFormat format) {

		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
		case FORMULA:
			return cell.getStringCellValue();
		case BLANK:
			return "";
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return format.format(cell.getDateCellValue());
			}
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
		case STRING:
			return cell.getStringCellValue();
		default:
			return "";
		}
	}

	public static void export(Workbook workbook, String fileName, HttpServletResponse response) throws Exception {
		if (workbook == null)
			throw new RuntimeException("导出EXCEL异常");
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setCharacterEncoding("utf-8"); 
			if (fileName != null) {
				String contentDisposition = "attachment;filename=" + new String(fileName.getBytes("utf8"), "iso8859-1") + ".xls";
				response.addHeader("Content-Disposition", contentDisposition);
			}
			workbook.write(out);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("导出EXCEL异常");
		} finally {
			if (out != null) {
				out.close();
			}
			workbook = null;
		}
	}
	
	public static void exportExcel(List<?> list, String title,
			String sheetName, Class<?> pojoClass, String fileName,
			boolean isCreateHeader, HttpServletResponse response) throws Exception {
		ExportParams exportParams = new ExportParams(title, sheetName);
		exportParams.setCreateHeadRows(isCreateHeader);
		defaultExport(list, pojoClass, fileName, response, exportParams);
	}
	
	public static void exportExcel(List<?> list, String title,
			String sheetName, Class<?> pojoClass, String fileName,
			HttpServletResponse response) throws Exception {
		defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
	}
	
	private static void defaultExport(List<?> list, Class<?> pojoClass,
			String fileName, HttpServletResponse response,
			ExportParams exportParams) throws Exception{
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams,
				pojoClass, list);
		if (workbook != null)
		export(workbook, fileName, response);
	}
	
	/**
	 * 复制sheet,并在第一列加入信息
	 * 
	 * @param createSheet
	 * @param row
	 *            源行
	 * @param rowIndex
	 *            源下标
	 * @param msg
	 *            其他信息(错误信息等)
	 */
	public static void copySheetRow(Sheet createSheet, Row row, int rowIndex, String msg) {
		if (null == createSheet || null == row)
			return;
		Row createRow = createSheet.createRow(rowIndex);

		createRow.createCell(0);
		createRow.getCell(0).setCellValue(msg);
		SimpleDateFormat formater = new SimpleDateFormat();
		formater.applyPattern("yyyy-MM-dd HH:mm:ss");
		int lastCellNum = row.getLastCellNum();
		Cell cell = null;
		for (int i = 0; i < lastCellNum; i++) {
			String cellVal = "";
			createRow.createCell(i + 1);
			cell = row.getCell(i);
			if (null != cell) {
				cellVal = ExcelUtils.getStringVal(cell, formater);
			}
			createRow.getCell(i + 1).setCellValue(cellVal);
		}
	}
	
	public static String formatMatch(MultipartFile file, String fileName,
			List<SysDict> resultFields) {
		try {
			return examineFormat(file, fileName, resultFields);
		} catch (Exception e) {
			return "Excel表格校验错误";
		}
	}
	
	/**
	 * 校验格式
	 * 
	 * @param excelFile
	 * @param fileName
	 *            文件名
	 * @param resultFields
	 * @return
	 */
	public static String examineFormat(MultipartFile excelFile, String fileName, List<SysDict> columns)
			throws Exception {
		if (null == excelFile || StringUtils.isBlank(fileName) || null == columns || columns.isEmpty())
			return null;
		boolean is03excel = fileName.matches("^.+\\.(?i)(xls)$"); // 03版:xls,07版:xlsx
		boolean correctFormat = true;
		try(InputStream inputStream = excelFile.getInputStream()){
			Workbook workbook = (Workbook) (is03excel ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream));
			Sheet sheet = workbook.getSheetAt(0);// 获取首个Sheet表
			if (null == sheet)
				return "Excel表格为空";
			// 获取物理行数
			int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
			if (physicalNumberOfRows <= 1)
				return "Excel数据为空"; // 行数大于1(默认从第二行读取)
			Row titleRow = sheet.getRow(0); // 第一行(表头)
			if (null == titleRow)
				return "Excel无表头数据";
			int cellNumber = titleRow.getPhysicalNumberOfCells();// 获取单元格个数
			if (cellNumber < 0 || cellNumber != columns.size())
				return "Excel数据列为空或数据列与模板字段不相符";
			Cell cell = null; // 单个单元格
			String cellValue = null;
			SysDict column = null;
			for (int i = 0; i < cellNumber; i++) {
				cell = titleRow.getCell(i);
				if (cell == null) {
					correctFormat = false;
					break;
				}
				cellValue = cell.getStringCellValue();
				if (StringUtils.isBlank(cellValue)) {
					correctFormat = false;
					break;
				}
				column = getTableField(columns, cellValue);
				if (null == column) {
					correctFormat = false;
					break;
				}
			}
		}
		return correctFormat ? null : "Excel数据校验错误";
	}
	
	private static SysDict getTableField(List<SysDict> fields, String titleValue){
		for (SysDict field : fields) {
			if(StringUtils.equals(field.getLabel(), titleValue)){
				return field;
			}
		}
		return null;
	}
	
	public static Workbook createExcelTempalte(Class<?> pojoClass, String sheetName){
		List<MemberInfo> list = new ArrayList<>();
		MemberInfo info = new MemberInfo();
		info.setTrueName("张三");
		info.setBirthday(new Date());
		info.setEducation(4);
		info.setField1("转介绍");
		info.setHeight(175);
		info.setHomeTown("广东深圳");
		info.setMarriage(1);
		info.setMobile("13800138000");
		info.setSalary(7);
		info.setSex(0);
		info.setWeight(165);
		info.setWorkCity("广东深圳");
		list.add(info);
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, sheetName),
				pojoClass, list);
		List<Excel> columns = AnnontationUtils.getAnnotationsFromFields(Excel.class, pojoClass);
		Sheet sheet = workbook.getSheetAt(0);
		Row titleRow = sheet.getRow(0);
		String[] items = null;
		for (int columnNum=0,len=titleRow.getPhysicalNumberOfCells();columnNum<len;columnNum++) {
			Excel column = columns.get(columnNum);
			if(column.replace().length > 0){
				items = new String[column.replace().length];
				for (int i=0,j=column.replace().length;i<j;i++) {
					items[i] = column.replace()[i].split("_")[0];
				}
				sheet.addValidationData(setDataValidation(sheet, items, 1, 5000, columnNum ,columnNum));
			}
		}
		return workbook;
	}
	
	/**
     * 
     * @Title: setDataValidation 
     * @Description: 下拉列表元素不多的情况(255以内的下拉)
     * @param @param sheet
     * @param @param textList
     * @param @param firstRow
     * @param @param endRow
     * @param @param firstCol
     * @param @param endCol
     * @param @return
     * @return DataValidation
     * @throws
     */
    private static DataValidation setDataValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {

        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        //DVConstraint constraint = new DVConstraint();
        constraint.setExplicitListValues(textList);
        
        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList((short) firstRow, (short) endRow, (short) firstCol, (short) endCol);
    
        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        //DataValidation data_validation = new DataValidation(regions, constraint);
    
        return data_validation;
    }
    
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws Exception{
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        }catch (NoSuchElementException e){
            throw new RuntimeException("excel文件为空");
        } catch (Exception e) {
            throw new RuntimeException("解析异常. reason:"+e.getMessage());
        }
        return list;
    }
    
    public static List<ExcelExportEntity> convertEntity(Class<?> pojoClass, String title){
    	List<ExcelExportEntity> result = new ArrayList<>();
    	if(title != null){
    		result.add(new ExcelExportEntity(title, "errormsg", 20));
    	}
    	Field[] fields = pojoClass.getDeclaredFields();
		if(fields == null || fields.length == 0) return result;
		Excel annotation = null;
		for (Field field : fields) {
			annotation = field.getAnnotation(Excel.class);
			if(annotation == null)
				continue;
			ExcelExportEntity entity = new ExcelExportEntity(annotation.name(), field.getName());
			entity.setReplace(annotation.replace());
			entity.setWidth(annotation.width());
			entity.setFormat(annotation.format());
			//entity.setType(annotation.type());
			result.add(entity);
		}
		return result;
    }
	
}
