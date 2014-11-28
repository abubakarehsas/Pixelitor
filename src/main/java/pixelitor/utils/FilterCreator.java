/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a skeleton of source code for a filter
 */
public class FilterCreator extends JPanel {
    private final JTextField nameTextField;
    private final JCheckBox guiCB;
    private final JCheckBox parametrizedGuiCB;
    private final JCheckBox copySrcCB;

    private final ParamPanel[] paramPanels = new ParamPanel[10];
    private final JCheckBox pixelLoopCB;
    private final JCheckBox proxyCB;
    private final JTextField proxyNameTF;
    private final JCheckBox edgeActionCB;
    private final JCheckBox interpolationCB;
    private final JCheckBox centerSelectorCB;
    private final JCheckBox colorCB;
    private final JCheckBox gradientCB;
    private final JCheckBox angleParamCB;

    private FilterCreator() {
        setLayout(new GridBagLayout());

        GridBagHelper.addLabel(this, "Name:", 0, 0);
        nameTextField = new JTextField(20);
        GridBagHelper.addLastControl(this, nameTextField);

        GridBagHelper.addLabel(this, "GUI:", 0, 1);
        guiCB = new JCheckBox();
        guiCB.setSelected(true);
        GridBagHelper.addControl(this, guiCB);

        GridBagHelper.addLabel(this, "Parametrized GUI:", 2, 1);
        parametrizedGuiCB = new JCheckBox();
        parametrizedGuiCB.setSelected(true);
        parametrizedGuiCB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (parametrizedGuiCB.isSelected()) {
                    guiCB.setSelected(true);
                }
            }
        });
        GridBagHelper.addControl(this, parametrizedGuiCB);

        GridBagHelper.addLabel(this, "Copy Src -> Dest:", 4, 1);
        copySrcCB = new JCheckBox();
        GridBagHelper.addControl(this, copySrcCB);

        GridBagHelper.addLabel(this, "Angle Param:", 6, 1);
        angleParamCB = new JCheckBox();
        GridBagHelper.addControl(this, angleParamCB);

        GridBagHelper.addLabel(this, "Pixel Loop:", 0, 2);
        pixelLoopCB = new JCheckBox();
        GridBagHelper.addControl(this, pixelLoopCB);

        GridBagHelper.addLabel(this, "Proxy Filter:", 2, 2);
        proxyCB = new JCheckBox();
//        proxyCB.setSelected(true);
        proxyCB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                proxyNameTF.setEnabled(proxyCB.isSelected());
            }
        });
        GridBagHelper.addControl(this, proxyCB);

        GridBagHelper.addLabel(this, "Proxy Name:", 4, 2);
        proxyNameTF = new JTextField(10);
        proxyNameTF.setEnabled(proxyCB.isSelected());
        GridBagHelper.addControl(this, proxyNameTF);

        GridBagHelper.addLabel(this, "Center Selector:", 0, 3);
        centerSelectorCB = new JCheckBox();
        GridBagHelper.addControl(this, centerSelectorCB);

        GridBagHelper.addLabel(this, "Edge Action:", 2, 3);
        edgeActionCB = new JCheckBox();
        GridBagHelper.addControl(this, edgeActionCB);

        GridBagHelper.addLabel(this, "Interpolation:", 4, 3);
        interpolationCB = new JCheckBox();
        GridBagHelper.addControl(this, interpolationCB);

        GridBagHelper.addLabel(this, "Color:", 6, 3);
        colorCB = new JCheckBox();
        GridBagHelper.addControl(this, colorCB);

        GridBagHelper.addLabel(this, "Gradient:", 8, 3);
        gradientCB = new JCheckBox();
        GridBagHelper.addControl(this, gradientCB);


        for (int i = 0; i < paramPanels.length; i++) {
            GridBagHelper.addLabel(this, "Param " + (i + 1) + ':', 0, i + 4);
            ParamPanel pp = new ParamPanel();
            paramPanels[i] = pp;
            GridBagHelper.addLastControl(this, pp);
        }
    }

    private ParameterInfo[] getParameterInfoArray() {
        List<ParameterInfo> piList = new ArrayList<>();
        for (ParamPanel panel : paramPanels) {
            ParameterInfo pi = panel.getParameterInfo();
            if (pi != null) {
                piList.add(pi);
            }
        }
        return piList.toArray(new ParameterInfo[piList.size()]);
    }

    public static void showInDialog(final Frame owner) {
        final FilterCreator filterCreator = new FilterCreator();
        OKCancelDialog d = new OKCancelDialog(filterCreator, owner, "Filter Creator", "Show Source", "Close") {
            @Override
            protected void dialogAccepted() {
                super.dialogAccepted();

                String s = filterCreator.createFilterSource();
                JTextArea ta = new JTextArea(s);
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(sp.getPreferredSize().width + 50, 500));
                GUIUtils.showTextDialog(sp, "Source", s);
            }

            @Override
            protected void dialogCanceled() {
                super.dialogCanceled();
                dispose();
            }
        };
        d.setVisible(true);
    }

    public static class ParamPanel extends JPanel {
        private final JTextField nameTextField;
        private final JTextField minTextField;
        private final JTextField maxTextField;
        private final JTextField defaultTextField;

        private ParamPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new JLabel("Name:"));
            nameTextField = new JTextField(20);
            add(nameTextField);
            add(new JLabel("Min:"));
            minTextField = new JTextField("0", 5);
            add(minTextField);
            add(new JLabel("Max:"));
            maxTextField = new JTextField("100", 5);
            add(maxTextField);
            add(new JLabel("Default:"));
            defaultTextField = new JTextField("0", 5);
            add(defaultTextField);
        }

        private ParameterInfo getParameterInfo() {
            String name = nameTextField.getText().trim();
            if (!name.isEmpty()) {
                int min = Integer.parseInt(minTextField.getText());
                int max = Integer.parseInt(maxTextField.getText());
                int defaultValue = Integer.parseInt(defaultTextField.getText());
                return new ParameterInfo(name, min, max, defaultValue);
            } else {
                return null;
            }
        }
    }

    private String createFilterSource() {
        boolean parametrizedGui = parametrizedGuiCB.isSelected();
        boolean gui = guiCB.isSelected();
        boolean copySrc = copySrcCB.isSelected();
        String name = nameTextField.getText();
        boolean pixelLoop = pixelLoopCB.isSelected();
        boolean proxy = proxyCB.isSelected();
        String proxyName = proxyNameTF.getText();

        boolean angleParam = angleParamCB.isSelected();
        boolean center = centerSelectorCB.isSelected();
        boolean edge = edgeActionCB.isSelected();
        boolean color = colorCB.isSelected();
        boolean gradient = gradientCB.isSelected();
        boolean interpolation = interpolationCB.isSelected();

        ParameterInfo[] params = getParameterInfoArray();

        return getCode(parametrizedGui, gui, copySrc, name, pixelLoop, proxy, proxyName, angleParam, center, edge, color, gradient, interpolation, params);
    }

    private static String getCode(boolean parametrizedGui, boolean gui, boolean copySrc, String name, boolean pixelLoop, boolean proxy, String proxyName, boolean angleParam, boolean center, boolean edge, boolean color, boolean gradient, boolean interpolation, ParameterInfo[] params) {
        String retVal = addImports(name, pixelLoop, parametrizedGui, proxy, proxyName);
        String className = name.replaceAll(" ", "");

        retVal += addSuperClass(gui, parametrizedGui, className, proxy);

        if (gui && parametrizedGui) {
            retVal += addParamsDeclaration(center, angleParam, edge, color, gradient, interpolation, params);
        }

        if (proxy) {
            retVal += "\n    private " + proxyName + " filter;\n";
        }

        retVal += addConstructor(name, gui, parametrizedGui, className, copySrc, proxy, edge, interpolation, params);
        retVal += addTransform(pixelLoop, proxy, proxyName, center, angleParam, edge, interpolation, params);
        retVal += addGetAdjustPanel(gui, parametrizedGui, className);

        return retVal;
    }

    private static String addGetAdjustPanel(boolean gui, boolean parametrizedGui, String className) {
        String retVal = "";
        if (gui && (!parametrizedGui)) {
            retVal += "\n    @Override\n";
            retVal += "    public AdjustPanel getAdjustPanel() {\n";
            retVal += "        return new " + className + "Adjustments(this);\n";
            retVal += "    }\n";
        }

        retVal += '}';
        return retVal;
    }

    private static String addTransform(boolean pixelLoop, boolean proxy, String proxyName, boolean center, boolean angleParam, boolean edgeAction, boolean interpolation, ParameterInfo[] params) {
        String retVal = "";
        retVal += "\n    @Override\n";
        retVal += "    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {\n";

        for (ParameterInfo param : params) {
            String paramName = param.getParamVariableName();
            String variableName = param.getVariableName();
            retVal += "       int " + variableName + " =  " + paramName + ".getValue();\n";
        }


        if (pixelLoop) {
            retVal += "        int[] srcData = ImageUtils.getPixelsAsArray(src);\n";
            retVal += "        int[] destData = ImageUtils.getPixelsAsArray(dest);\n";
        }

        if (proxy) {
            retVal += "       if(filter == null) {\n";
            retVal += "           filter = new " + proxyName + "();\n";
            retVal += "       }\n";

            retVal += '\n';

            if (center) {
                retVal += "        filter.setCenterX(center.getRelativeX());\n";
                retVal += "        filter.setCenterY(center.getRelativeY());\n";
            }
            if (edgeAction) {
                retVal += "        filter.setEdgeAction(edgeActionParam.getValue());\n";
            }
            if (interpolation) {
                retVal += "        filter.setInterpolation(interpolationParam.getValue());\n";
            }

            retVal += '\n';

            if (angleParam) {
                retVal += "        filter.setAngle(angleParam.getValueInRadians());\n";
            }

            retVal += "        dest = filter.filter(src, dest);\n";
        }

        retVal += "        return dest;\n";
        retVal += "    }\n";
        return retVal;
    }

    private static String addSuperClass(boolean gui, boolean parametrizedGui, String className, boolean proxy) {
        String retVal = "";
        String superClassName1;
        if (gui) {
            if (parametrizedGui) {
                superClassName1 = "FilterWithParametrizedGUI";
            } else {
                superClassName1 = "FilterWithGUI";
            }
        } else {
            superClassName1 = "Filter";
        }
        String superClassName = superClassName1;

//        if (proxy) {
//            className = "JH" + className;
//        }
        retVal += "public class " + className;
        retVal += " extends " + superClassName;
        retVal += " {\n";
        return retVal;
    }

    private static String addImports(String name, boolean pixelLoop, boolean parametrizedGUI, boolean proxy, String proxyName) {
        String retVal = "";
        if (pixelLoop) {
            retVal += "import pixelitor.utils.ImageUtils;\n";
        }
        retVal += "import pixelitor.filters.gui.ParamSet;\n";
        retVal += "import pixelitor.filters.gui.RangeParam;\n";
        if (parametrizedGUI) {
            retVal += "import pixelitor.filters.FilterWithParametrizedGUI;\n";
        }

        retVal += "\nimport java.awt.image.BufferedImage;\n";
        retVal += "\n/**\n";

        if (proxy) {
            retVal += " * " + name + " based on " + proxyName + '\n';
        } else {
            retVal += " * " + name + '\n';
        }

        retVal += " */\n";
        return retVal;
    }

    private static String addConstructor(String name, boolean gui, boolean parametrizedGui, String className, boolean copySrc, boolean proxy, boolean edge, boolean interpolation, ParameterInfo... params) {
        String retVal = "";
//        if (proxy) {
//            className = "JH" + className;
//        }
        retVal += "\n    public " + className + "() {\n";

        retVal += "        super(\"" + name + "\", true, false);\n";

        if (copySrc) {
            retVal += "        copySrcToDstBeforeRunning = true;\n";
        }

        if (gui && parametrizedGui) {
            retVal += addParamSetToConstructor(edge, interpolation, params);
        }
        retVal += "    }\n";
        return retVal;
    }

    private static String addParamSetToConstructor(boolean edge, boolean interpolation, ParameterInfo... params) {
        String retVal = "";
        if (params.length == 1) {
            retVal += "        setParamSet(new ParamSet(" + params[0].getParamVariableName() + "));\n";
        } else {
            retVal += "        setParamSet(new ParamSet(\n";
            for (int i = 0; i < params.length; i++) {
                ParameterInfo param = params[i];
                String paramName = param.getParamVariableName();
                retVal += "            " + paramName;
                if (i < params.length - 1 || edge || interpolation) {
                    retVal += ',';
                }
                retVal += '\n';
            }
            if(edge) {
                retVal += "            edgeActionParam,\n";
            }
            if(interpolation) {
                retVal += "            interpolationParam\n";
            }

            retVal += "        ));\n";
        }
        return retVal;
    }

    private static String addParamsDeclaration(boolean center, boolean angleParam, boolean edge, boolean color, boolean gradient, boolean interpolation, ParameterInfo... params) {
        String retVal = "";
        for (ParameterInfo param : params) {
            String paramVarName = param.getParamVariableName();

            retVal += "    private RangeParam " + paramVarName + " = new RangeParam(\"" + param.getName() + "\", " + param.getMin() + ", " + param.getMax() + ", " + param.getDefaultValue() + ");";

            retVal += '\n';
        }

        if (center) {
            retVal += "    private ImagePositionParam centerParam = new ImagePositionParam(\"Center\");\n";
        }
        if (angleParam) {
            retVal += "    private AngleParam angleParam = new AngleParam(\"Angle\", 0);\n";
        }
        if (edge) {
            retVal += "    private IntChoiceParam edgeActionParam =  IntChoiceParam.getEdgeActionChoices();\n";
        }
        if (interpolation) {
            retVal += "    private IntChoiceParam interpolationParam = IntChoiceParam.getInterpolationChoices();\n";
        }
        if (color) {
            retVal += "    private ColorParam colorParam = new ColorParam(\"Color:\", Color.WHITE, false, false);\n";
        }
        if (gradient) {
            retVal += "    private float[] defaultThumbPositions = new float[]{0f, 1f};\n";
            retVal += "    private Color[] defaultValues = new Color[]{Color.BLACK, Color.WHITE};\n";
            retVal += "    private GradientParam gradientParam = new GradientParam(\"Colors:\", defaultThumbPositions, defaultValues);\n";
        }
        return retVal;
    }

    public static class ParameterInfo {
        String name;
        String paramVariableName;
        String variableName;
        int min;
        int max;
        int defaultValue;

        public ParameterInfo(String name, int min, int max, int defaultValue) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;

            String tmp = name.replaceAll(" ", "");
            this.variableName = tmp.substring(0, 1).toLowerCase() + tmp.substring(1);
            this.paramVariableName = variableName + "Param";
        }

        private String getName() {
            return name;
        }

        private String getParamVariableName() {
            return paramVariableName;
        }

        private String getVariableName() {
            return variableName;
        }

        private int getMin() {
            return min;
        }

        private int getMax() {
            return max;
        }

        private int getDefaultValue() {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        // showInDialog(null);
    }
}