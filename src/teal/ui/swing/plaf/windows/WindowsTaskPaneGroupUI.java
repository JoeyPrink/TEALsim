/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teal.ui.swing.plaf.windows;

import teal.ui.swing.JTaskPaneGroup;
import teal.ui.swing.plaf.basic.BasicTaskPaneGroupUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * Windows implementation of the WindowsTaskPaneUI.
 */
public class WindowsTaskPaneGroupUI extends BasicTaskPaneGroupUI {

  /**
	 * 
	 */
	private static final long serialVersionUID = -7970745088643530288L;

  public static ComponentUI createUI(JComponent c) {
    return new WindowsTaskPaneGroupUI();
  }

  private static int TITLE_HEIGHT = 25;
  private static int ROUND_HEIGHT = 5;
    
  protected Border createPaneBorder() {
    return new XPPaneBorder();
  }
  
  /**
   * Overriden to paint the background of the component but keeping the rounded
   * corners.
   */
  public void update(Graphics g, JComponent c) {
    if (c.isOpaque()) {
      g.setColor(c.getParent().getBackground());
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
      g.setColor(c.getBackground());
      g.fillRect(0, ROUND_HEIGHT, c.getWidth(), c.getHeight() - ROUND_HEIGHT);
    }
    paint(g, c);
  }

  protected int getTitleHeight() {
    return TITLE_HEIGHT;
  }

  /**
   * The border of the taskpane group paints the "text", the "icon", the
   * "expanded" status and the "special" type.
   *  
   */
  class XPPaneBorder extends PaneBorder {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1951536704084111312L;

	protected void paintTitleBackground(JTaskPaneGroup group, Graphics g) {
      if (group.isSpecial()) {
        g.setColor(specialTitleBackground);
        g.fillRoundRect(
          0,
          0,
          group.getWidth(),
          ROUND_HEIGHT * 2,
          ROUND_HEIGHT,
          ROUND_HEIGHT);
        g.fillRect(
          0,
          ROUND_HEIGHT,
          group.getWidth(),
          TITLE_HEIGHT - ROUND_HEIGHT);
      } else {
        Paint oldPaint = ((Graphics2D)g).getPaint();
        GradientPaint gradient =
          new GradientPaint(
            0f,
            group.getWidth() / 2,
            titleBackgroundGradientStart,
            group.getWidth(),
            TITLE_HEIGHT,
            titleBackgroundGradientEnd);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ((Graphics2D)g).setRenderingHint(
          RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);
        ((Graphics2D)g).setPaint(gradient);
        g.fillRoundRect(
          0,
          0,
          group.getWidth(),
          ROUND_HEIGHT * 2,
          ROUND_HEIGHT,
          ROUND_HEIGHT);
        g.fillRect(
          0,
          ROUND_HEIGHT,
          group.getWidth(),
          TITLE_HEIGHT - ROUND_HEIGHT);
        ((Graphics2D)g).setPaint(oldPaint);
      }
    }

    protected void paintExpandedControls(JTaskPaneGroup group, Graphics g) {
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

      int ovalSize = TITLE_HEIGHT - 2 * ROUND_HEIGHT;

      if (group.isSpecial()) {
        g.setColor(specialTitleBackground.brighter());
        g.drawOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);
      } else {
        g.setColor(titleBackgroundGradientStart);
        g.fillOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);

        g.setColor(titleBackgroundGradientEnd.darker());
        g.drawOval(
          group.getWidth() - TITLE_HEIGHT,
          ROUND_HEIGHT - 1,
          ovalSize,
          ovalSize);
      }

      Color paintColor;
      if (mouseOver) {
        if (group.isSpecial()) {
          paintColor = specialTitleOver;
        } else {
          paintColor = titleOver;
        }
      } else {
        if (group.isSpecial()) {
          paintColor = specialTitleForeground;
        } else {
          paintColor = titleForeground;
        }
      }

      ChevronIcon chevron;
      if (group.isExpanded()) {
        chevron = new ChevronIcon(true);
      } else {
        chevron = new ChevronIcon(false);
      }
      int chevronX =
        group.getWidth()
          - TITLE_HEIGHT
          + ovalSize / 2
          - chevron.getIconWidth() / 2;
      int chevronY =
        ROUND_HEIGHT + (ovalSize / 2 - chevron.getIconHeight()) - 1;
      g.setColor(paintColor);
      chevron.paintIcon(group, g, chevronX, chevronY);
      chevron.paintIcon(
        group,
        g,
        chevronX,
        chevronY + chevron.getIconHeight() + 1);

      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void paintBorder(
      Component c,
      Graphics g,
      int x,
      int y,
      int width,
      int height) {

      JTaskPaneGroup group = (JTaskPaneGroup)c;

      // paint the title background
      paintTitleBackground(group, g);

      // paint the the toggles
      paintExpandedControls(group, g);

      // paint the title text and icon
      Color paintColor;
      if (mouseOver) {
        if (group.isSpecial()) {
          paintColor = specialTitleOver;
        } else {
          paintColor = titleOver;
        }
      } else {
        if (group.isSpecial()) {
          paintColor = specialTitleForeground;
        } else {
          paintColor = titleForeground;
        }
      }

      // focus painted same color as text
      if (group.hasFocus()) {
        g.setColor(paintColor);
        BasicGraphicsUtils.drawDashedRect(g, 3, 3, width - 6, TITLE_HEIGHT - 6);
      }
      
      paintTitle(
        group,
        g,
        paintColor,
        3,
        0,
        c.getWidth() - TITLE_HEIGHT - 3,
        TITLE_HEIGHT);
    }
  }

}
