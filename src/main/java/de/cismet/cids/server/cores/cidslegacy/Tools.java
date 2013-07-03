/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.cores.cidslegacy;

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.io.FileUtils;

import java.io.File;

import java.util.HashMap;
import java.util.Set;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.data.CidsAttribute;
import de.cismet.cids.data.CidsClass;
import de.cismet.cids.data.configkeys.AttributeConfig;
import de.cismet.cids.data.configkeys.ClassConfig;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Tools {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   mc  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsClass createClassFromCidsLegacyMetaClass(final MetaClass mc) {
        final CidsClass c = new CidsClass(mc.getTableName(), mc.getDomain());
        if (mc.getName() != null) {
            c.setConfigAttribute(ClassConfig.Keys.Name, mc.getName());
        }
        if (mc.getPolicy() != null) {
            c.setConfigAttribute(ClassConfig.Keys.Policy, mc.getPolicy().getName());
        }
        if (mc.getAttributePolicy() != null) {
            c.setConfigAttribute(ClassConfig.Keys.AttributePolicy, mc.getAttributePolicy().getName());
        }
        c.setConfigAttribute(ClassConfig.Keys.PK_Field, mc.getPrimaryKey());
//        c.setConfigAttribute(ClassConfig.Keys.ClassIcon,???);
//        c.setConfigAttribute(ClassConfig.Keys.ObjectIcon,???);
        if (mc.isArrayElementLink()) {
            c.setConfigFlag(ClassConfig.FlagKeys.ArrayLink);
        }
        if (mc.isIndexed()) {
            c.setConfigFlag(ClassConfig.FlagKeys.Indexed);
        }

        if (mc.getRenderer() != null) {
            c.setConfigAttribute(ClassConfig.XPKeys.RendererXP, mc.getRenderer());
        }
        if (mc.getComplexEditor() != null) {
            c.setConfigAttribute(ClassConfig.XPKeys.EditorXP, mc.getComplexEditor());
        }

        // Iterate through all class-attributes
        final ClassAttribute[] classAttributes = mc.getAttribs();
        for (final ClassAttribute ca : classAttributes) {
            c.setOtherConfigAttribute(ca.getName(), ca.getValue());
        }
        // Iterate through all field (without recursion)

        final HashMap fields = mc.getMemberAttributeInfos();
        for (final Object entry : fields.entrySet()) {
            final MemberAttributeInfo mai = (MemberAttributeInfo)((java.util.Map.Entry)entry).getValue();
            mai.getFieldName();
            mai.getName();
            mai.getJavaclassname();

            final CidsAttribute ca = createCidsAttributeFromCidsLegacyMemberAttributeInfo(mc.getDomain(),
                    mai,
                    c.getKey());
            c.putAttribute(ca);
        }
        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain    DOCUMENT ME!
     * @param   mai       DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsAttribute createCidsAttributeFromCidsLegacyMemberAttributeInfo(final String domain,
            final MemberAttributeInfo mai,
            final String classKey) {
        final CidsAttribute ca = new CidsAttribute(mai.getFieldName(), classKey);
        if (mai.isVisible()) {
            ca.setConfigFlag(AttributeConfig.FlagKeys.Visible);
        }
        if (mai.isArray()) {
            ca.setConfigFlag(AttributeConfig.FlagKeys.Array);
        }

        if (mai.isExtensionAttribute()) {
            ca.setConfigFlag(AttributeConfig.FlagKeys.ExtensionAttribute);
        }

        if (mai.isIndexed()) {
            ca.setConfigFlag(AttributeConfig.FlagKeys.Indexed);
        }
        if (mai.isOptional()) {
            ca.setConfigFlag(AttributeConfig.FlagKeys.Optional);
        }
        if (mai.getName() != null) {
            ca.setConfigAttribute(AttributeConfig.Keys.Name, mai.getName());
        }
//        if (mai.getArrayKeyFieldName() != null) {
//            ca.setConfigAttribute(AttributeConfig.Keys.ArrayKeyFieldName, mai.getArrayKeyFieldName());
//        }
        if (mai.getDefaultValue() != null) {
            ca.setConfigAttribute(AttributeConfig.Keys.DefaultValue, mai.getDefaultValue());
        }

        ca.setConfigAttribute(AttributeConfig.Keys.Position, mai.getPosition());

        if (mai.isArray()) {
            final int arrayId = mai.getForeignKeyClassId();
            final MetaClass arrayTableClass = ClassCacheMultiple.getMetaClass(domain, arrayId);

            final HashMap arrayTableClassFields = arrayTableClass.getMemberAttributeInfos();
            for (final Object entry : arrayTableClassFields.entrySet()) {
                final MemberAttributeInfo arrayTableClassField = (MemberAttributeInfo)((java.util.Map.Entry)entry)
                            .getValue();
                if (arrayTableClassField.isForeignKey()) {
                    final MetaClass arrayEntryClass = ClassCacheMultiple.getMetaClass(
                            domain,
                            arrayTableClassField.getForeignKeyClassId());
                    ca.setConfigAttribute(
                        AttributeConfig.Keys.ReferenceType,
                        "/"
                                + arrayEntryClass.getDomain()
                                + "."
                                + arrayEntryClass.getTableName());
                    break;
                }
            }
        } else if (mai.isForeignKey()) {
            if (mai.getForeignKeyClassId() < 0) {
                ca.setConfigFlag(AttributeConfig.FlagKeys.Array);
            } else {
                ca.setConfigFlag(AttributeConfig.FlagKeys.ForeignKey);
            }
            final MetaClass foreignClass = ClassCacheMultiple.getMetaClass(
                    domain,
                    Math.abs(mai.getForeignKeyClassId()));
            ca.setConfigAttribute(
                AttributeConfig.Keys.ReferenceType,
                "/"
                        + foreignClass.getDomain()
                        + "."
                        + foreignClass.getTableName());
        } else {
            if (mai.getJavaclassname() != null) {
                ca.setConfigAttribute(AttributeConfig.Keys.Javaclassname, mai.getJavaclassname());
            }
        }

        return ca;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        // final ObjectMapper om = CidsBean.getCidsBeanIntraObjectCacheMapper();
        final ObjectMapper om = CidsBean.getCidsBeanObjectMapper();
        DevelopmentTools.initSessionManagerFromRestfulConnectionOnLocalhost(
            "crisma",
            "Administratoren",
            "admin",
            "cismet");
        final HashMap hm = ClassCacheMultiple.getTableNameHashtableOfClassesForOneDomain("crisma");
        final Set<String> tableNames = hm.keySet();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        final String dir = "/Users/thorsten/Desktop/crisma-api-demo";
        for (final String table : tableNames) {
            final MetaClass mc = ClassCacheMultiple.getMetaClass("crisma", table);
            if (!mc.isArrayElementLink()) {
                final CidsClass c = createClassFromCidsLegacyMetaClass(mc);
                FileUtils.write(new File(dir + "/classes/" + table + ".json"), om.writeValueAsString(c));
                FileUtils.forceMkdir(new File(dir + "/objects/" + table));
                final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                        "crisma",
                        "Administratoren",
                        "admin",
                        "cismet",
                        table,
                        100);
                System.out.println(table + " ...");
                for (final CidsBean cb : beans) {
                    FileUtils.write(new File(dir + "/objects/" + table + "/" + cb.getPrimaryKeyValue() + ".json"),
                        om.writeValueAsString(cb));
                }
                System.out.println("... " + beans.length + " written");
            }
        }

        System.exit(0);
    }
}
