package daogenerator;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Creador de DAO's para aplicaciones Java y MySQL.
 *
 * @author BurnKill
 */
public class DAOGenerator {

    private PrintStream ps = System.out;
    private String tabla;
    private int numeroPropiedades;
    private String[] nombrePropiedades;
    private String[] tipoPropiedades;
    private String[] encabezados;

    public DAOGenerator(PrintStream ps, String tabla, int numeroPropiedades, String[] nombrePropiedades, String[] tipoPropiedades, String[] encabezados) {
        this.ps = ps;
        this.tabla = tabla;
        this.numeroPropiedades = numeroPropiedades;
        this.nombrePropiedades = nombrePropiedades;
        this.tipoPropiedades = tipoPropiedades;
        this.encabezados = encabezados;
    }

    public DAOGenerator(String tabla, int numeroPropiedades, String[] nombrePropiedades, String[] tipoPropiedades, String[] encabezados) {
        this.tabla = tabla;
        this.numeroPropiedades = numeroPropiedades;
        this.nombrePropiedades = nombrePropiedades;
        this.tipoPropiedades = tipoPropiedades;
        this.encabezados = encabezados;
    }

    public static void main(String[] args) {
        DAOGenerator gen;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce nombre de tabla");
        String tabla = scanner.next();

        System.out.println("Introduce numero de variables sin contar ID");
        int numeroPropiedades = scanner.nextInt();

        //VARIABLES Y TIPOS
        String[] nombrePropiedades = new String[numeroPropiedades];
        String[] tipoPropiedades = new String[numeroPropiedades];
        String[] encabezados = new String[numeroPropiedades];

        System.out.println("Ingresa tipo de variable, su nombre y display en encabezado. Repite cuantas veces necesites. Siempre separa presionado [INTRO]");

        for (int i = 0; i < numeroPropiedades; i ++) {
            tipoPropiedades[i] = scanner.next();
            nombrePropiedades[i] = scanner.next();
            encabezados[i] = scanner.next();
        }
        gen = new DAOGenerator(System.out, tabla, numeroPropiedades, nombrePropiedades, tipoPropiedades, encabezados
        );
    }

    public void imprimeDao() {
        imprime1Inicio();
        imprime2Variables();
        imprime3Inserta();
        imprime4Consultar();
        imprime5Carga();
        imprime6Elimina();
        imprime7Actualiza();
    }

    private void imprime1Inicio() {
        imprimeln("/*\n"
                + " * Java Database Connectivity Code Generator v1.0\n"
                + " * Author: David Vazquez\n"
                + " */\n"
                + "package dao;\n"
                + "\n"
                + "import java.sql.Connection;\n"
                + "import java.sql.PreparedStatement;\n"
                + "import java.sql.ResultSet;\n"
                + "import javax.swing.table.DefaultTableModel;\n"
                + "import pojo." + tabla + "POJO;\n"
                + "\n"
                + "public class " + tabla + "DAO {");
    }

    private void imprime2Variables() {
        imprime(" private static final String TABLE = \"" + tabla + "\";" + "\n");
        imprime("private static final String SQL_INSERT = \"INSERT INTO \" + TABLE + \"(");

        for (int i = 0; i < numeroPropiedades; i ++) {
            if (i == numeroPropiedades - 1) {
                imprime(nombrePropiedades[i] + "");
            } else {
                imprime(nombrePropiedades[i] + ", ");
            }
        }
        imprime(") VALUES (");

        for (int i = 0; i < numeroPropiedades; i ++) {
            if (i == numeroPropiedades - 1) {
                imprime("?");
            } else {
                imprime("?,");
            }

        }

        imprimeln(")\";");

        //SQL_QUERY_ALL
        imprimeln("private static final String SQL_QUERY_ALL = \"Select * from \" + TABLE;");
        //SQL_QUERY
        imprimeln("private static final String SQL_QUERY = \"Select * from \" + TABLE + \" where id" + tabla + "=?\";");
        //SQL UPDATE
        imprime("private static final String SQL_UPDATE = \"UPDATE \" + TABLE + \" set ");

        for (int i = 0; i < numeroPropiedades; i ++) {
            if (i == numeroPropiedades - 1) {
                imprime(nombrePropiedades[i] + "=? ");
            } else {
                imprime(nombrePropiedades[i] + "=?, ");
            }
        }
        imprimeln("where id" + tabla + "=?\";");

        //SQL_DELETE
        imprimeln(" private static final String SQL_DELETE = \"Delete from \" + TABLE\n"
                + "            + \" where id" + tabla + "=?\";");
    }

    private void imprime3Inserta() {
        imprimeln(" public static int inserta" + tabla + "(" + tabla + "POJO pojo) {\n"
                + "        Connection con = null;\n"
                + "        PreparedStatement st = null;\n"
                + "        try {\n"
                + "            con = Conexion.getConnection();\n"
                + "            st = con.prepareStatement(SQL_INSERT);");
        for (int i = 0; i < numeroPropiedades; i ++) {
            imprimeln("st.set" + tipoPropiedades[i] + "(" + (i + 1) + ", pojo.get" + Character.toUpperCase(nombrePropiedades[i].charAt(0)) + nombrePropiedades[i].substring(1) + "());");
        }

        imprimeln("int id = st.executeUpdate();\n"
                + "            return id;\n"
                + "        } catch (Exception e) {\n"
                + "            imprimeln(\"Error al insertar \" + e);\n"
                + "            return 0;\n"
                + "        } finally {\n"
                + "            Conexion.close(con);\n"
                + "            Conexion.close(st);\n"
                + "        }\n"
                + "    }");
    }

    private void imprime4Consultar() {
        imprimeln(" public static " + tabla + "POJO consultar(String id) {\n"
                + "        Connection con = null;\n"
                + "        PreparedStatement st = null;\n"
                + "        " + tabla + "POJO pojo = new " + tabla + "POJO();");

        imprimeln(" try {\n"
                + "\n"
                + "            con = Conexion.getConnection();\n"
                + "            st = con.prepareStatement(SQL_QUERY);\n"
                + "            st.setString(1, id);\n"
                + "            ResultSet rs = st.executeQuery();\n"
                + "            while (rs.next()) {");

        imprimeln(" pojo.setId" + tabla + "(rs.getInt(\"id" + tabla + "\"));");

        for (int i = 0; i < numeroPropiedades; i ++) {
            imprimeln(" pojo.set" + Character.toUpperCase(nombrePropiedades[i].charAt(0)) + nombrePropiedades[i].substring(1) + "(rs.get" + tipoPropiedades[i] + "(\"" + nombrePropiedades[i] + "\"));");
        }

        imprimeln("}\n"
                + "        } catch (Exception e) {\n"
                + "            imprimeln(\"Error al consultar \" + e);\n"
                + "        } finally {\n"
                + "            Conexion.close(con);\n"
                + "            Conexion.close(st);\n"
                + "        }\n"
                + "        return pojo;\n}");
    }

    private void imprime5Carga() {

        imprime("public static DefaultTableModel cargarTabla() {\n"
                + "        Connection con = null;\n"
                + "        PreparedStatement st = null;\n"
                + "        String encabezados[] = {");
        imprime("\"" + "ID" + "\", ");
        for (int i = 0; i < numeroPropiedades; i ++) {
            if (i == numeroPropiedades - 1) {
                imprime("\"" + encabezados[i] + "\"");
            } else {
                imprime("\"" + encabezados[i] + "\", ");
            }
        }
        imprimeln("};");

        imprimeln(" DefaultTableModel dt = null;\n"
                + "        try {\n"
                + "            con = Conexion.getConnection();\n"
                + "            st = con.prepareStatement(SQL_QUERY);\n"
                + "            dt = new DefaultTableModel();\n"
                + "            dt.setColumnIdentifiers(encabezados);\n"
                + "            ResultSet rs = st.executeQuery();\n"
                + "            while (rs.next()) {\n"
                + "                Object ob[] = new Object[" + (numeroPropiedades + 1) + "];");

        imprimeln(" ob[0] = rs.getObject(\"id" + tabla + "\");");
        for (int i = 0; i < numeroPropiedades; i ++) {
            imprimeln("ob[" + (i + 1) + "] = rs.getObject(\"" + nombrePropiedades[i] + "\");");
        }
        imprimeln(" dt.addRow(ob);\n"
                + "            }\n"
                + "            rs.close();\n"
                + "        } catch (Exception e) {\n"
                + "            imprimeln(\"Error al consultar \" + e);\n"
                + "        } finally {\n"
                + "            Conexion.close(con);\n"
                + "            Conexion.close(st);\n"
                + "            \n"
                + "        }\n"
                + "        return dt;\n"
                + "    }");
    }

    private void imprime6Elimina() {
        imprimeln("public static boolean eliminar" + tabla + "(String id) {\n"
                + "        Connection con = null;\n"
                + "        PreparedStatement st = null;\n"
                + "        try {\n"
                + "            con = Conexion.getConnection();\n"
                + "            st = con.prepareStatement(SQL_DELETE);\n"
                + "            st.setString(1, id);\n"
                + "            int num = st.executeUpdate();\n"
                + "            if (num == 0) {\n"
                + "                return false;\n"
                + "            }\n"
                + "        } catch (Exception e) {\n"
                + "            imprimeln(\"Error al eliminar = \" + e);\n"
                + "            return false;\n"
                + "        } finally {\n"
                + "            Conexion.close(con);\n"
                + "            Conexion.close(st);\n"
                + "        }\n"
                + "        return true;\n"
                + "    }");
    }

    private void imprime7Actualiza() {
        imprimeln("public static boolean actualiza" + tabla + "(" + tabla + "POJO pojo) {\n"
                + "        Connection con = null;\n"
                + "        PreparedStatement st = null;\n"
                + "        try {\n"
                + "            con = Conexion.getConnection();\n"
                + "            st = con.prepareStatement(SQL_UPDATE);");
        int i = 0;
        for (; i < numeroPropiedades; i ++) {

            imprimeln(" st.set" + tipoPropiedades[i] + "(" + (i + 1) + ", pojo.get" + Character.toUpperCase(nombrePropiedades[i].charAt(0)) + nombrePropiedades[i].substring(1) + "());");
        }
        imprimeln("    st.setInt(" + (i + 1) + ", pojo.getId" + tabla + "());");
        imprimeln("int num = st.executeUpdate();\n"
                + "            if (num == 0) {\n"
                + "                return false;\n"
                + "            }\n"
                + "        } catch (Exception e) {\n"
                + "            imprimeln(\"Error al actualizar = \" + e);\n"
                + "            return false;\n"
                + "        } finally {\n"
                + "            Conexion.close(con);\n"
                + "            Conexion.close(st);\n"
                + "        }\n"
                + "        return true;\n"
                + "    }");
    }

    private void imprimeln(String contenido) {
        ps.println(contenido);
    }

    private void imprime(String contenido) {
        ps.print(contenido);
    }

}
