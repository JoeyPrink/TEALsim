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
package teal.ui.swing.plaf.basic;

import teal.ui.swing.JTaskPane;
import teal.ui.swing.PercentLayout;
import teal.ui.swing.plaf.TaskPaneUI;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

/**
 * Base implementation of the <code>JTaskPane</code> UI.
 */
public class BasicTaskPaneUI extends TaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicTaskPaneUI();
  }
  
  protected JTaskPane taskPane;

  public void installUI(JComponent c) {
    super.installUI(c);
    taskPane = (JTaskPane)c;
    taskPane.setLayout(new PercentLayout(PercentLayout.VERTICAL, 14));
    taskPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    taskPane.setOpaque(true);

    if (taskPane.getBackground() == null
      || taskPane.getBackground() instanceof ColorUIResource) {
      taskPane.setBackground(UIManager.getColor("TaskPane.background"));
    }
  }

}
