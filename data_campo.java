import org.compiere.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.DB;
import org.compiere.process.*;
import org.adempiere.base.event.*;
import org.compiere.model.MTable;
import org.adempiere.model.GenericPO;

int record_ID = A_Record_ID;
String tableName = "Tabela_Controle";

// Obtenha o PO da tabela e do registro
PO po = MTable.get(A_Ctx, tableName).getPO(record_ID, A_TrxName);

if (po == null) {
    return "Registro não encontrado.";
}

// Agora sim, você pode acessar o campo:
Object dataObj = po.get_Value("Data_Controle");

// Verifica se é nulo ou faz o cast
if (dataObj == null) {
    return "Data_Controle está vazio.";
}

return dataObj.toString();
