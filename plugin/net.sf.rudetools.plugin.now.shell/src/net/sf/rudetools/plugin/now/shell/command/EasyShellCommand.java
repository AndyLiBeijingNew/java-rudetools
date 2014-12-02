/*
 * Copyright (C) 2004 - 2013 by Marcel Schoen and Andre Bossert
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sf.rudetools.plugin.now.shell.command;

import net.sf.rudetools.plugin.now.shell.EasyShellMyAction;
import net.sf.rudetools.plugin.now.shell.EditorPropertyTester;
import net.sf.rudetools.plugin.now.shell.actions.EasyShellAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class EasyShellCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		EasyShellAction action = EditorPropertyTester
				.hasResourceSelection(activePart);
		if (action != null) {
			EasyShellMyAction act = new EasyShellMyAction(event.getCommand()
					.getId());
			action.run((IAction) act);
		}
		return null;
	}
}
