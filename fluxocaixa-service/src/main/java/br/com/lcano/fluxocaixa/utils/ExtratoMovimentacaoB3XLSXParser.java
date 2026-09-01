package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtratoMovimentacaoB3XLSXParser {

    private static final Logger log = LoggerFactory.getLogger(ExtratoMovimentacaoB3XLSXParser.class);
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoMovimentacaoB3DTO> parse(byte[] conteudo) {
        List<ExtratoMovimentacaoB3DTO> itens = new ArrayList<>();
        int rejeitadas = 0;

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

                    if (tipoOperacao.isEmpty() && produto.isEmpty() && dataMovimentacao == null) {
                        continue;
                    }
                    if (tipoOperacao.isEmpty() || dataMovimentacao == null) {
                        rejeitadas++;
                        log.warn("Movimentacao B3: linha {} ignorada (tipo de operacao ou data ausente).", row.getRowNum() + 1);
                        continue;
                    }

                    BigDecimal quantidade = getCellNumerico(row, 5);
                    BigDecimal precoUnitario = getCellNumerico(row, 6);
                    BigDecimal precoTotal = getCellNumericoObrigatorio(row, 7);

                    itens.add(new ExtratoMovimentacaoB3DTO(
                            tipoOperacao, dataMovimentacao, tipoMovimentacao,
                            produto, quantidade, precoUnitario, precoTotal));
                } catch (Exception e) {
                    rejeitadas++;
                    log.warn("Movimentacao B3: linha {} ignorada ({}).", row.getRowNum() + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        if (rejeitadas > 0) {
            log.warn("Movimentacao B3: {} linha(s) nao reconhecida(s), {} importada(s).", rejeitadas, itens.size());
        }
        return itens;
    }

    private static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }
        return new DataFormatter().formatCellValue(cell).trim();
    }

    private static Date getCellDate(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        if (isDateCell(cell)) {
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

    private static boolean isDateCell(Cell cell) {
        CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
        return type == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell);
    }

    /** Aceita 0 quando a celula esta vazia (quantidade/preco unitario podem faltar). */
    private static BigDecimal getCellNumerico(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal valor = lerNumero(cell);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    /** Rejeita a linha (lanca) quando o valor total nao pode ser lido, em vez de importar como zero. */
    private static BigDecimal getCellNumericoObrigatorio(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        BigDecimal valor = cell == null ? null : lerNumero(cell);
        if (valor == null) {
            throw new IllegalArgumentException("valor total ausente ou nao numerico");
        }
        return valor;
    }

    private static BigDecimal lerNumero(Cell cell) {
        CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
        if (type == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (type == CellType.STRING) {
            String texto = cell.getStringCellValue().trim();
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return ValorParser.parse(texto);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
