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

import java.awt.Component;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.plaf.PanelUI;

/**
 * Pluggable UI for <code>JTaskPaneGroup</code>.
 *  
 */
public class TaskPaneGroupUI extends PanelUI implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = -2233354334399171153L;

/**
   * Called by the component when an action is added to the component through
   * the {@link teal.ui.swing.JTaskPaneGroup#add(Action)} method.
   * 
   * @param action
   * @return a component built from the action.
   */
  public Component createAction(Action action) {
    return new JButton(action);
  }

}
