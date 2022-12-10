package util;

import com.beust.jcommander.*;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class CustomHelpPageFormatter extends DefaultUsageFormatter {
    public CustomHelpPageFormatter(JCommander commander) {
        super(commander);
    }

    public void appendAllParametersDetails(StringBuilder out, int indentCount, String indent, List<ParameterDescription> sortedParameters) {
        if (sortedParameters.size() > 0) {
            out.append(indent).append("  Options:\n");
        }

        int prefixIndent = 0;
        Iterator var6 = sortedParameters.iterator();

        ParameterDescription pd;
        WrappedParameter parameter;
        String prefix;
        while (var6.hasNext()) {
            pd = (ParameterDescription) var6.next();
            parameter = pd.getParameter();
            prefix = (parameter.required() ? "* " : "       ") + pd.getNames();
            if (prefix.length() > prefixIndent) {
                prefixIndent = prefix.length();
            }
        }

        var6 = sortedParameters.iterator();

        while (var6.hasNext()) {
            pd = (ParameterDescription) var6.next();
            parameter = pd.getParameter();
            prefix = (parameter.required() ? "* " : "  ") + pd.getNames();
            out.append(indent).append("  ").append(prefix).append(s(prefixIndent - prefix.length())).append(" ");
            int initialLinePrefixLength = indent.length() + prefixIndent + 3;
            String description = pd.getDescription();
            Object def = pd.getDefault();
            String displayedDef;
            if (pd.isDynamicParameter()) {
                displayedDef = "(syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value)";
                description = description + (description.length() == 0 ? "" : " ") + displayedDef;
            }

            String valueList;
            if (def != null && !pd.isHelp()) {
                displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
                valueList = "(default: " + (parameter.password() ? "********" : displayedDef) + ")";
                description = description + (description.length() == 0 ? "" : " ") + valueList;
            }

            Class type = pd.getParameterized().getType();
            if (type.isEnum()) {
                valueList = EnumSet.allOf(type).toString();
                if (!description.contains("Options: " + valueList)) {
                    String possibleValues = "(values: " + valueList + ")";
                    description = description + (description.length() == 0 ? "" : " ") + possibleValues;
                }
            }

            this.wrapDescription(out, indentCount + prefixIndent - 3, initialLinePrefixLength, description);
            out.append("\n");
        }

    }
}
