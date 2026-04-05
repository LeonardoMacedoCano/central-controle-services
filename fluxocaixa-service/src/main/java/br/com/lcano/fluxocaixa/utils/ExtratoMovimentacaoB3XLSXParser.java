package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtratoMovimentacaoB3XLSXParser {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoMovimentacaoB3DTO> parse(byte[] conteudo) {
        List<ExtratoMovimentacaoB3DTO> itens = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(conteudo))) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                try {
                    String tipoOperacao = getCellString(row, 0);
                    Date dataMovimentacao = getCellDate(row, 1);
                    String tipoMovimentacao = getCellString(row, 2);
                    String produto = getCellString(row, 3);
                    BigDecimal quantidade = parseMonetario(getCellString(row, 5));
                    BigDecimal precoUnitario = parseMonetario(getCellString(row, 6));
                    BigDecimal precoTotal = parseMonetario(getCellString(row, 7));

                    if (tipoOperacao.isEmpty() || dataMovimentacao == null) continue;

                    itens.add(new ExtratoMovimentacaoB3DTO(
                            tipoOperacao, dataMovimentacao, tipoMovimentacao,
                            produto, quantidade, precoUnitario, precoTotal));
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        return itens;
    }

    private static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return new DataFormatter().formatCellValue(cell).trim();
    }

    private static Date getCellDate(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            return sdf.parse(new DataFormatter().formatCellValue(cell).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static BigDecimal parseMonetario(String valor) {
        if (valor == null || valor.isBlank() || valor.equals("-")) return BigDecimal.ZERO;
        try {
            return new BigDecimal(
                    valor.replace("R$", "")
                            .replace(".", "")
                            .replace(",", ".")
                            .trim()
            );
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
