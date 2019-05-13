/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.performance;

import eu.h2020.symbiote.semantics.mapping.data.DataMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;
import eu.h2020.symbiote.semantics.mapping.model.RetentionPolicy;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import eu.h2020.symbiote.semantics.mapping.utils.DataGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class PerformanceTest {

    private static final int iterations = 100;

    private MappingConfig config;

    private List<Pair<Mapping, Query>> rewritingTestcases;

    @Before
    public void initialize() throws ParseException, IOException, URISyntaxException {
        config = new MappingConfig();
        config.setRetentionPolicy(RetentionPolicy.RemoveMatchedInput);
        rewritingTestcases = Arrays.asList(
                new Pair(Utils.getMapping(Constants.MAPPING_PERFORMANCE_1),
                        Utils.getQuery(Constants.QUERY_PERFORMANCE_1)),
                new Pair(Utils.getMapping(Constants.MAPPING_PERFORMANCE_2),
                        Utils.getQuery(Constants.QUERY_PERFORMANCE_2)),
                new Pair(Utils.getMapping(Constants.MAPPING_PERFORMANCE_3),
                        Utils.getQuery(Constants.QUERY_PERFORMANCE_3))
        );
        warmUpRun();
    }

    private void warmUpRun() {
        try {
            rewritingTestcases.forEach(x -> mapSafe(x.getKey(), x.getValue()));
            mapSafe(Utils.getMapping(Constants.MAPPING_PERFORMANCE_1), new DataGenerator().generateData(10, false, false));
        } catch (IOException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stream<Long> measureTime(Consumer<Void> function, int iterations, TimeUnit unit) {
        return LongStream.rangeClosed(1, iterations).map(x -> measureTime(function, unit)).boxed();
    }

    private long measureTime(Consumer<Void> function, TimeUnit unit) {
        long startTime = System.nanoTime();
        function.accept(null);
        long endTime = System.nanoTime();
        return unit.convert(endTime - startTime, TimeUnit.NANOSECONDS);
    }
   

    private void mapSafe(Mapping mapping, Query query) {
        try {
            Query map = new SparqlMapper().map(query, mapping, config);
            String foo = "";
        } catch (UnsupportedMappingException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mapSafe(Mapping mapping, Model model) {
        try {
            Model result = new DataMapper().map(model, mapping, config);
        } catch (UnsupportedMappingException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Workbook prepareQueryRewritingResult(int runs) throws FileNotFoundException, IOException {
        final Workbook workbook = new XSSFWorkbook(new FileInputStream(new File("src/test/resources/evaluation_template_queryrewriting.xlsx")));
        Sheet sheet = workbook.getSheetAt(0);
        String[] formulars = new String[]{
            sheet.getRow(0).getCell(1).getCellFormula(),
            sheet.getRow(1).getCell(1).getCellFormula(),
            sheet.getRow(2).getCell(1).getCellFormula(),
            sheet.getRow(3).getCell(1).getCellFormula(),
            sheet.getRow(4).getCell(1).getCellFormula(),
            sheet.getRow(5).getCell(1).getCellFormula(),
            sheet.getRow(6).getCell(1).getCellFormula(),};
        // copy formulars for all runs on master sheet
        for (int i = 1; i < runs; i++) {
            for (int j = 0; j < formulars.length; j++) {
                Cell cell = sheet.getRow(j).getCell(1 + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellType(CellType.FORMULA);
                sheet.setArrayFormula(copyFormula(sheet, formulars[j], i, 0), new CellRangeAddress(j, j, i+1, i+1));
            }
            Cell cell = sheet.getRow(formulars.length).getCell(1 + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellValue(1+i);
            cell.setCellStyle(sheet.getRow(7).getCell(1).getCellStyle());
        }
        // copy sheet
        for (int i = 1; i < rewritingTestcases.size(); i++) {
            String newSheetname = workbook.cloneSheet(0).getSheetName();
            workbook.setSheetName(workbook.getSheetIndex(newSheetname), "Query " + (i + 1));
        }
        return workbook;
    }

    private static String copyFormula(Sheet sheet, String formula, int coldiff, int rowdiff) {

        XSSFEvaluationWorkbook workbookWrapper
                = XSSFEvaluationWorkbook.create((XSSFWorkbook) sheet.getWorkbook());
        Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
                sheet.getWorkbook().getSheetIndex(sheet));
        for (int i = 0; i < ptgs.length; i++) {
            if (ptgs[i] instanceof RefPtgBase) { // base class for cell references
                RefPtgBase ref = (RefPtgBase) ptgs[i];
                if (ref.isColRelative()) {
                    ref.setColumn(ref.getColumn() + coldiff);
                }
                if (ref.isRowRelative()) {
                    ref.setRow(ref.getRow() + rowdiff);
                }
            } else if (ptgs[i] instanceof AreaPtgBase) { // base class for range references
                AreaPtgBase ref = (AreaPtgBase) ptgs[i];
                if (ref.isFirstColRelative()) {
                    ref.setFirstColumn(ref.getFirstColumn() + coldiff);
                }
                if (ref.isLastColRelative()) {
                    ref.setLastColumn(ref.getLastColumn() + coldiff);
                }
                if (ref.isFirstRowRelative()) {
                    ref.setFirstRow(ref.getFirstRow() + rowdiff);
                }
                if (ref.isLastRowRelative()) {
                    ref.setLastRow(ref.getLastRow() + rowdiff);
                }
            }
        }

        formula = FormulaRenderer.toFormulaString(workbookWrapper, ptgs);
        return formula;
    }

    @Test
    public void testQueryRewritingPerformance() throws ParseException, UnsupportedMappingException {
        try {
            int noRuns = 11;
            final Workbook workbook = prepareQueryRewritingResult(noRuns);
            for (int runs = 0; runs < noRuns; runs++) {                
                int count = 0;
                for (Pair<Mapping, Query> testcase : rewritingTestcases) {
                    Sheet sheet = workbook.getSheetAt(count);
                    List<Long> executionTimes = measureTime(x -> mapSafe(testcase.getKey(), testcase.getValue()), iterations, TimeUnit.MICROSECONDS).collect(Collectors.toList());
                    DoubleStatistics statistics = executionTimes.stream()
                            .map(x -> x.doubleValue())
                            .collect(DoubleStatistics.collector());
                    for (int i = 0; i < executionTimes.size(); i++) {
                        sheet.getRow(8+i).getCell(1 + runs, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(executionTimes.get(i));
                    }
                    System.out.println("run: " + runs + ", iterations: " + iterations + ", avg: " + statistics.getAverage() + ", std: " + statistics.getStandardDeviation() + ", min: " + statistics.getMin() + ", max: " + statistics.getMax());
                    count++;
                }

            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();
            FileOutputStream out = new FileOutputStream((new File("src/test/resources/evaluation_queryrewriting_" + dateFormat.format(date) + ".xlsx")));
            workbook.setForceFormulaRecalculation(true);
            workbook.write(out);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(PerformanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testDataTransformationPerformance() throws ParseException, UnsupportedMappingException, IOException, URISyntaxException {
        final Workbook workbook = new XSSFWorkbook(new FileInputStream(new File("src/test/resources/evaluation_template_mapping.xlsx")));
        Logger.getGlobal().setLevel(Level.INFO);
        org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
        for (int runs = 1; runs <= 2; runs++) {
            List<Pair<Integer, Model>> dataSets = Files.list(Paths.get(Utils.class.getClassLoader().getResource(Constants.DATA_DIR).toURI()))
                    .filter(x -> FilenameUtils.getExtension(x.toString()).equalsIgnoreCase(Constants.DATA_EXTENSION))
                    .map(x -> (Pair<Integer, Model>) new Pair(Integer.parseInt(x.toFile().getName().replace("." + Constants.DATA_EXTENSION, "")), RDFDataMgr.loadModel(x.toString(), Lang.TURTLE)))
                    .collect(Collectors.toList());

            String[][] dataPlot = new String[dataSets.size()][iterations + 1];
            for (int i = 0; i < dataPlot.length; i++) {
                for (int j = 0; j < dataPlot[i].length; j++) {
                    dataPlot[i][j] = "";
                }
            }
            List<Mapping> mappings = Arrays.asList(
                    Utils.getMapping(Constants.MAPPING_PERFORMANCE_1),
                    Utils.getMapping(Constants.MAPPING_PERFORMANCE_2),
                    Utils.getMapping(Constants.MAPPING_PERFORMANCE_3));
            Sheet sheet = workbook.getSheet("Run " + runs);
            short startColumn = 1;
            int startRow = 9;
            int testSizeCount = 0;
            for (Pair<Integer, Model> dataSet : dataSets) {
                int testSize = dataSet.getKey();

                Model data = dataSet.getValue();
                for (int m = 0; m < mappings.size(); m++) {

                    final Mapping mapping = mappings.get(m);
                    List<Long> executionTimes = measureTime(x -> mapSafe(mapping, data), iterations, TimeUnit.MILLISECONDS).collect(Collectors.toList());

                    DoubleStatistics statistics = executionTimes.stream().map(x -> x.doubleValue()).collect(DoubleStatistics.collector());
                    System.out.println("test size: " + testSize + ", mapping: " + (m + 1) + ", iterations: " + iterations + ", avg: " + statistics.getAverage() + ", std: " + statistics.getStandardDeviation() + ", min: " + statistics.getMin() + ", max: " + statistics.getMax());
//                    if (testSize < 1000) {
//                        dataPlot[m][0] += "\t\t\t" + testSize;
//                    } else {
//                        dataPlot[m][0] += "\t\t\t" + (testSize / 1000) + "k";
//                    }

                    for (int i = 0; i < executionTimes.size(); i++) {
                        int currentRow = startRow + i;
                        int currentColumn = startColumn + (8 * m) + testSizeCount;
                        if (sheet.getRow(currentRow) == null) {
                            sheet.createRow(currentRow);
                        }
                        if (sheet.getRow(currentRow).getCell(currentColumn) == null) {
                            sheet.getRow(currentRow).createCell(currentColumn);
                        }
                        sheet.getRow(currentRow).getCell(currentColumn).setCellValue(executionTimes.get(i));
//                        dataPlot[m][i + 1] += "\t\t\t" + executionTimes.get(i);
                    }
                }
                testSizeCount++;
            }
//            for (int i = 0; i < dataPlot.length; i++) {
//                System.out.println("data for mapping " + (i + 1));
//                for (String line : dataPlot[i]) {
//                    System.out.println(line.trim());
//                }
//            }
//            System.out.println("===============================================================");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        try (FileOutputStream out = new FileOutputStream((new File("src/test/resources/evaluation_transformation_" + dateFormat.format(date) + ".xlsx")))) {
//            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            workbook.setForceFormulaRecalculation(true);
            workbook.write(out);
        } catch (Exception ex) {

        }
    }

    static class DoubleStatistics extends DoubleSummaryStatistics {

        private double sumOfSquare = 0.0d;
        private double sumOfSquareCompensation; // Low order bits of sum
        private double simpleSumOfSquare; // Used to compute right sum for
        // non-finite inputs

        @Override
        public void accept(double value) {
            super.accept(value);
            double squareValue = value * value;
            simpleSumOfSquare += squareValue;
            sumOfSquareWithCompensation(squareValue);
        }

        public DoubleStatistics combine(DoubleStatistics other) {
            super.combine(other);
            simpleSumOfSquare += other.simpleSumOfSquare;
            sumOfSquareWithCompensation(other.sumOfSquare);
            sumOfSquareWithCompensation(other.sumOfSquareCompensation);
            return this;
        }

        private void sumOfSquareWithCompensation(double value) {
            double tmp = value - sumOfSquareCompensation;
            double velvel = sumOfSquare + tmp; // Little wolf of rounding error
            sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
            sumOfSquare = velvel;
        }

        public double getSumOfSquare() {
            double tmp = sumOfSquare + sumOfSquareCompensation;
            if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
                return simpleSumOfSquare;
            }
            return tmp;
        }

        public final double getStandardDeviation() {
            long count = getCount();
            double sumOfSquare = getSumOfSquare();
            double average = getAverage();
            return count > 0 ? Math.sqrt((sumOfSquare - count * Math.pow(average, 2)) / (count - 1)) : 0.0d;
        }

        public static Collector<Double, ?, DoubleStatistics> collector() {
            return Collector.of(DoubleStatistics::new, DoubleStatistics::accept, DoubleStatistics::combine);
        }

    }
}
