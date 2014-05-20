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
package teal.ui.swing.plaf;

import teal.ui.swing.JTaskPane;
import teal.ui.swing.plaf.aqua.AquaLookAndFeelAddons;
import teal.ui.swing.plaf.basic.BasicLookAndFeelAddons;
import teal.ui.swing.plaf.metal.MetalLookAndFeelAddons;
import teal.ui.swing.plaf.windows.WindowsClassicLookAndFeelAddons;
import teal.ui.swing.plaf.windows.WindowsLookAndFeelAddons;
import teal.util.OS;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Addon for <code>JTaskPane</code>. <br>
 *  
 */
public class JTaskPaneAddon implements ComponentAddon {

  public String getName() {
    return "JTaskPane";
  }

  public void initialize(LookAndFeelAddons addon) {
    
    if (addon instanceof BasicLookAndFeelAddons) {
      addon.loadDefaults(new Object[] {
        JTaskPane.UI_CLASS_ID,
        "teal.ui.swing.plaf.basic.BasicTaskPaneUI",
        "TaskPane.background",
        UIManager.getColor("Desktop.background")
      });
    }
    
    if (addon instanceof MetalLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPane.background",
        MetalLookAndFeel.getDesktopColor()
      });
    }
    
    if (addon instanceof WindowsLookAndFeelAddons) {     
      String xpStyle = OS.getWindowsVisualStyle();
      Object background;
      if ("homestead".equalsIgnoreCase(xpStyle)) {        
        background = new ColorUIResource(201, 215, 170);
      } else if ("metallic".equalsIgnoreCase(xpStyle)) {
        background = new ColorUIResource(192, 195, 209);
      } else {        
        background = new ColorUIResource(117, 150, 227);
      }      
      addon.loadDefaults(new Object[]{
        "TaskPane.background",
        background,
      });
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPane.background",
        UIManager.getColor("List.background")
      });      
    }
    
    if (addon instanceof AquaLookAndFeelAddons) {
      addon.loadDefaults(new Object[]{
        "TaskPane.background",
        new ColorUIResource(238, 238, 238),
      });            
    }
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

}
