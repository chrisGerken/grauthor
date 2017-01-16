package com.gerken.xaa.mpe.editor;


import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.statushandlers.StatusManager;
import org.gramar.IGramar;
import org.gramar.IGramarApplicationStatus;
import org.gramar.IGramarStatus;
import org.gramar.eclipse.platform.EclipseFileStore;
import org.gramar.eclipse.platform.EclipsePlatform;
import org.gramar.eclipse.ui.util.StatusFactory;
import org.gramar.model.XmlModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.gerken.xaa.mpe.core.AbstractFormPage;
import com.gerken.xaa.mpe.core.AbstractToolSection;
import com.gerken.xaa.mpe.core.ModelAccess;

public class OverviewToolslSection extends AbstractToolSection implements IHyperlinkListener {

	public OverviewToolslSection(AbstractFormPage page, Composite parent) {
		super(page, parent);
	}

	public String getTextContent() {

		String patternId = ModelAccess.getAttribute(getModel(), "/xform/@xformId");
		String thisValidationSetting = "on";
		String nextValidationSetting = "off";
		boolean validate = getPage().getMpeEditor().getValidateModel();
		if (!validate) {
			thisValidationSetting = "off";
			nextValidationSetting = "on";
		}
		
		String content = "<form>" +
							"<li style=\"text\" bindent=\"5\"><a href=\"purposes.to.tokens\">Build tokens</a> for selected purposes</li>" +
//							"<li style=\"text\" bindent=\"5\"><a href=\"invoke.xaa.xform\">Generate</a> "+patternId+" xform</li>" +
							"<li style=\"text\" bindent=\"5\"><a href=\"invoke.xaa.gramar2\">Generate</a> "+patternId+" gramar</li>" +
							"<li style=\"text\" bindent=\"5\">Model validation is "+thisValidationSetting+".  Turn it <a href=\"toggle.validation\">"+nextValidationSetting+"</a> </li>" +
							"</form>";

		return content;
	}

	public void linkActivated(HyperlinkEvent e) {
		String href = e.getHref().toString();


//		if (href.equals("invoke.xaa.xform")) {
//			if (getPage().getMpeEditor().getConstraintManager().getCurrentProblems().size() > 0) {
//				boolean goon = MessageDialog.openConfirm(getShell(),"Errors Exist in Model","Errors in the model may cause the generation to fail.  Do you wish to continue?");
//				if (!goon) { return; }
//			}
//			try {
//				String patternId = ModelAccess.getAttribute(getModel(), "/xform/@xformId");
//				boolean goon = MessageDialog.openConfirm(getShell(),"Begin Generation","Generate "+patternId+" xform?");
//				if (!goon) { return; }
//					// Begin pattern invoke sample.xform
//				String contents = ModelFormatter.getInstance().format(getModel());
//				IStatus status = JET2Platform.runTransformOnString("com.gerken.xaa.xform", contents, new NullProgressMonitor());
//				if ((status.getSeverity() == IStatus.OK) | (status.getSeverity() == IStatus.INFO)) {
//					MessageDialog.openInformation(getShell(),"Pattern successfully applied",status.getMessage());
//				} else {
//					ErrorDialog.openError(getShell(),"Pattern applied with errors",status.getMessage(),status);
//				}
//					// End pattern invoke sample.xform
//			} catch (NullPointerException t) {
//				MessageDialog.openInformation(getShell(),"Missing Pattern",t.toString());
//			} catch (Throwable t) {
//				MessageDialog.openInformation(getShell(),"Exception thrown",t.toString());
//			}
//			return;
//		}
//
//		if (href.equals("invoke.xaa.gramar")) {
//			if (getPage().getMpeEditor().getConstraintManager().getCurrentProblems().size() > 0) {
//				boolean goon = MessageDialog.openConfirm(getShell(),"Errors Exist in Model","Errors in the model may cause the generation to fail.  Do you wish to continue?");
//				if (!goon) { return; }
//			}
//			try {
//				String patternId = ModelAccess.getAttribute(getModel(), "/xform/@xformId");
//				boolean goon = MessageDialog.openConfirm(getShell(),"Begin Generation","Generate "+patternId+" gramar?");
//				if (!goon) { return; }
//					// Begin pattern invoke sample.xform
//				String contents = ModelFormatter.getInstance().format(getModel());
//				IStatus status = JET2Platform.runTransformOnString("com.gerken.xaa.gramar.xform", contents, new NullProgressMonitor());
//				if ((status.getSeverity() == IStatus.OK) | (status.getSeverity() == IStatus.INFO)) {
//					MessageDialog.openInformation(getShell(),"Pattern successfully applied",status.getMessage());
//				} else {
//					ErrorDialog.openError(getShell(),"Pattern applied with errors",status.getMessage(),status);
//				}
//					// End pattern invoke sample.xform
//			} catch (NullPointerException t) {
//				MessageDialog.openInformation(getShell(),"Missing Pattern",t.toString());
//			} catch (Throwable t) {
//				MessageDialog.openInformation(getShell(),"Exception thrown",t.toString());
//			}
//			return;
//		}

		if (href.equals("invoke.xaa.gramar2")) {
			if (getPage().getMpeEditor().getConstraintManager().getCurrentProblems().size() > 0) {
				boolean goon = MessageDialog.openConfirm(getShell(),"Errors Exist in Model","Errors in the model may cause the generation to fail.  Do you wish to continue?");
				if (!goon) { return; }
			}
			try {
				String gramarId = ModelAccess.getAttribute(getModel(), "/xform/@xformId");
				boolean goon = false;
				if (getPage().getMpeEditor().getValidateModel()) {
					goon = MessageDialog.openConfirm(getShell(),"Begin Generation","Generate "+gramarId+" gramar?");
				} else {
					goon = MessageDialog.openConfirm(getShell(),"Model validation is off","Generate "+gramarId+" gramar even though model is not validated?");
				}
				if (!goon) { return; }
					// Begin pattern invoke sample.xform

				
				EclipsePlatform platform = new EclipsePlatform();
				IGramar gramar = platform.getGramar("com.gerken.xaa.gramar.gramar");						
				EclipseFileStore fileStore = (EclipseFileStore) platform.getDefaultFileStore();
				fileStore.setMinLogLevel(IGramarStatus.SEVERITY_INFO);
				IGramarApplicationStatus status = platform.apply(new XmlModel(getModel()), gramar, fileStore);

				if (!status.hadErrors()) {
					String msg = "Model accessed "+status.getModelAccesses()+" times.";  
					MessageDialog.openInformation(getShell(),"Gramar successfully generated", "See log for details");
					MultiStatus ms = StatusFactory.status(status.getContext(), 0, "Gramar applied successfully");
//					Activator.getDefault().getLog().log(ms);
				} else {
					MultiStatus ms = StatusFactory.status(status.getContext(), 0, "Gramar applied with errors");
					String content = status.mainProductionResult();
					ErrorDialog.openError(getShell(),"Gramar applied with errors",ms.getMessage(),ms);
				}
					// End pattern invoke sample.xform
			} catch (NullPointerException t) {
				MessageDialog.openInformation(getShell(),"Missing Pattern",t.toString());
			} catch (Throwable t) {
				MessageDialog.openInformation(getShell(),"Exception thrown",t.toString());
			}
			return;
		}

		if (href.equals("purposes.to.tokens")) {
			boolean confirm = MessageDialog.openConfirm(getShell(),"Build Tokens","Do you wish to build tokens from selected purposes?");
			if (!confirm) { return; }
			purposesToTokens();			
			return;
		}

		if (href.equals("toggle.validation")) {
			getPage().getMpeEditor().toggleValidateModel();
			if (getPage().getMpeEditor().getValidateModel()) {
				getPage().getMpeEditor().forceValidation();
			} else {
				getPage().getMpeEditor().getConstraintManager().resetProblems();
			}
//			refresh();
			((OverviewPage)getPage()).markStale();
			return;
		}

	}

	private void purposesToTokens() {
		Document doc = getPage().getModel();
		Node[] cf = ModelAccess.getNodes(doc,"/xform/group/createFile[@purposeAsTokenName=\"true\"]");
		Node xpath = ModelAccess.getNodes(doc, "/xform")[0];
		for (int n = 0; n < cf.length; n++) {
			String groupName = ModelAccess.getAttribute(cf[n], "../@name");
			String purpose = ModelAccess.getAttribute(cf[n], "@purpose");
			String oPath = ModelAccess.getAttribute( cf[n], "@oPath");
			int offset = oPath.lastIndexOf("/");
			if (offset > -1) {
				oPath = oPath.substring(offset+1);
			}
			offset = oPath.indexOf(".");
			if (offset > -1) {
				oPath = oPath.substring(0,offset);
			}
			String filename = oPath;
			
			Element replacement = xpath.getOwnerDocument().createElement("replacement");
			xpath.appendChild(replacement);
			replacement.setAttribute("newString","{$"+groupName+"/@"+purpose+"}");
			replacement.setAttribute("oldString",filename);

			Node group = ModelAccess.getNodes(cf[n], "..")[0];
			
			Element token = group.getOwnerDocument().createElement("token");
			group.appendChild(token);
			token.setAttribute("formula",filename);
			token.setAttribute("derived","true");
			token.setAttribute("name",purpose);
			token.setAttribute("desc","variable for "+filename);
			
			((Element)cf[n]).setAttribute("purposeAsTokenName", "false");
			
		}
		
	}

	public void linkEntered(HyperlinkEvent e) {}

	public void linkExited(HyperlinkEvent e) {}

	public boolean isPrimary() {
		return true;
	}

	public String getText() {
		return "Custom Actions";
	}
	
	public String getDescription() {
		return "Click to perform an action";
	}

	public boolean isSectionExpanded() {
		return true;
	}

}
