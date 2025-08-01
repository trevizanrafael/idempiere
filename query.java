import org.compiere.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.compiere.util.DB;
import org.compiere.process.*;
import org.adempiere.base.event.*;
import org.compiere.model.MTable;
import org.adempiere.model.GenericPO;

int record_ID = A_Record_ID;
String tableName = "Tabela_Controle";

// datas como Timestamp
Timestamp dataInicio = Timestamp.valueOf("2023-12-18 00:00:01");
Timestamp dataFim    = Timestamp.valueOf("2023-12-18 23:59:59");

// Formatar para string no padrão SQL com timezone UTC
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String dataInicioStr = sdf.format(dataInicio);
String dataFimStr    = sdf.format(dataFim);

String sql = "SELECT COUNT(*) FROM PAC_Monitoramento " +
             "WHERE Created >= '" + dataInicioStr + "+00' " +
             "AND Created <= '" + dataFimStr + "+00'";

int total = DB.getSQLValue(null, sql);

PO po = MTable.get(A_Ctx, tableName).getPO(record_ID, A_TrxName);

if (po == null) {
    return "Registro não encontrado.";
}

po.set_ValueOfColumn("Quantidade_Monitoramentos", new BigDecimal(total));
po.saveEx();

return "Registro atualizado com sucesso.";
