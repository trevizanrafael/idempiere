import org.compiere.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.compiere.util.DB;
import org.compiere.process.*;
import org.adempiere.base.event.*;
import org.compiere.model.MTable;
import org.adempiere.model.GenericPO;
int record_ID = A_Record_ID;
String tableName = "Tabela_Controle";

// cria po, e faz cast para string
PO po = MTable.get(A_Ctx, tableName).getPO(record_ID, A_TrxName);
if (po == null) {
    return "Registro não encontrado.";
}

Object dataObj = po.get_Value("Data_Controle");

if (dataObj == null) {
    return "Data_Controle está vazio.";
}

Timestamp data = (Timestamp) dataObj;

Calendar cal = Calendar.getInstance();
cal.setTime(data);
cal.set(Calendar.HOUR_OF_DAY, 0);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 1);
cal.set(Calendar.MILLISECOND, 0); // opcional, mas ajuda na precisão
Timestamp dataInicio = new Timestamp(cal.getTimeInMillis());

cal.set(Calendar.HOUR_OF_DAY, 23);
cal.set(Calendar.MINUTE, 59);
cal.set(Calendar.SECOND, 59);
cal.set(Calendar.MILLISECOND, 999);
Timestamp dataFim = new Timestamp(cal.getTimeInMillis());

// 5. Monta a query com as datas formatadas
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String dataInicioStr = sdf.format(dataInicio);
String dataFimStr = sdf.format(dataFim);

String sql = "SELECT COUNT(*) FROM PAC_Monitoramento " +
             "WHERE Created >= '" + dataInicioStr + "+00' " +
             "AND Created <= '" + dataFimStr + "+00'";

// 6. Executa a query
int total = DB.getSQLValue(null, sql);

po.set_ValueOfColumn("Quantidade_Monitoramentos_Exec", new BigDecimal(total));
po.saveEx();

String sqlNC = "SELECT COUNT(*) FROM PAC_Monitoramento " +
               "WHERE Created >= '" + dataInicioStr + "+00' " +
               "AND Created <= '" + dataFimStr + "+00' " +
               "AND ConformeNaoConforme = 'NC'";

int totalNC = DB.getSQLValue(null, sqlNC);

po.set_ValueOfColumn("Quantidade_Conformes", new BigDecimal(total-totalNC));
po.saveEx();

po.set_ValueOfColumn("Quantidade_Nao_Conformes", new BigDecimal(totalNC));
po.saveEx();

String sqlPD = "SELECT COUNT(*) FROM PAC_Monitoramento " +
               "WHERE Created >= '" + dataInicioStr + "+00' " +
               "AND Created <= '" + dataFimStr + "+00' " +
               "AND VerificacaoAcaoCorretiva= 'PD'";

int totalPD = DB.getSQLValue(null, sqlPD);

po.set_ValueOfColumn("Acao_Corretiva_Pendente", new BigDecimal(totalPD));
po.saveEx();

po.set_ValueOfColumn("Acao_Corretiva_Executada", new BigDecimal(totalNC-totalPD));
po.saveEx();

String sqlPD2 = "SELECT COUNT(*) FROM PAC_Monitoramento " +
               "WHERE Created >= '" + dataInicioStr + "+00' " +
               "AND Created <= '" + dataFimStr + "+00' " +
               "AND VerificacaoAcaoPreventiva= 'PD'";

int totalPD2 = DB.getSQLValue(null, sqlPD2);

