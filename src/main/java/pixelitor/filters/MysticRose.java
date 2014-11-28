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
package pixelitor.filters;

import com.jhlabs.composite.AddComposite;
import org.jdesktop.swingx.image.FastBlurFilter;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Mystic Rose
 */
public class MysticRose extends FilterWithParametrizedGUI {
    private RangeParam pointsParam = new RangeParam("Number of Points", 3, 42, 10);
    private RangeParam widthParam = new RangeParam("Line Width", 1, 10, 1);
    private RangeParam rotateParam = new RangeParam("Rotate", 0, 100, 0);
    private BooleanParam glow = new BooleanParam("Glow", false);

    public MysticRose() {
        super("Mystic Rose", false, false);
        setParamSet(new ParamSet(
                pointsParam,
                widthParam,
                rotateParam,
                glow
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();
        dest = new BufferedImage(width, height, src.getType());
        Graphics2D g2 = dest.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(widthParam.getValue()));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int numPoints = pointsParam.getValue();
        Point[] points = new Point[numPoints];
        double radius = 0.45 * Math.min(width, height);
        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;
        double startAngle = 2 * Math.PI / numPoints * rotateParam.getValueAsPercentage();
        for (int i = 0; i < points.length; i++) {
            double theta = startAngle + 2 * Math.PI * i / numPoints;
            points[i] = new Point((int)(halfWidth + radius * Math.cos(theta)), (int)(halfHeight + radius * Math.sin(theta)));
        }
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                if(i > j) { // draw only in one direction
                    g2.drawLine(points[i].x, points[i].y, points[j].x, points[j].y);
                }
            }
        }

        if(glow.getValue()) {
            BufferedImage copy = ImageUtils.copyImage(dest);
            FastBlurFilter fastBlur = new FastBlurFilter(widthParam.getValue());
            fastBlur.filter(copy, copy);
            g2.setComposite(new AddComposite(1.0f));
            g2.drawImage(copy, 0, 0, null);

//            GlowFilter glowFilter = new GlowFilter();
//            glowFilter.setRadius(widthParam.getValue());
//            glowFilter.filter(dest, dest);
        }
        g2.dispose();

        return dest;
    }
}