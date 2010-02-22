package com.izforge.izpack.integration;

import com.izforge.izpack.api.GuiId;
import com.izforge.izpack.compiler.data.CompilerData;
import com.izforge.izpack.installer.container.IInstallerContainer;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.data.UninstallData;
import com.izforge.izpack.installer.language.LanguageDialog;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for an installation
 */
public class InstallationTest extends AbstractInstallationTest
{

    @AfterMethod
    public void tearBinding()
    {
        applicationContainer.dispose();
        try
        {
            if (dialogFrameFixture != null)
            {
                dialogFrameFixture.cleanUp();
                dialogFrameFixture = null;
            }
        }
        finally
        {
            if (installerFrameFixture != null)
            {
                installerFrameFixture.cleanUp();
                installerFrameFixture = null;
            }
        }
    }

    @Test
    public void testHelloAndFinishPanels() throws Exception
    {
        compileInstallJar("helloAndFinish.xml", getWorkingDirectory("samples"));
        Image image = resourceManager.getImageIconResource("/img/JFrameIcon.png").getImage();
        assertThat(image, IsNull.<Object>notNullValue());

        installerContainer = applicationContainer.getComponent(IInstallerContainer.class);
        installerContainer.getComponent(LanguageDialog.class).initLangPack();
        installerFrameFixture = prepareFrameFixture();

        // Hello panel
        installerFrameFixture.requireSize(new Dimension(640, 480));
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        installerFrameFixture.requireVisible();
        // Finish panel
    }


    @Test(dependsOnMethods = "testHelloAndFinishPanels", enabled = false)
    public void testHelloAndFinishPanelsCompressed() throws Exception
    {
        System.out.println("Using file " + out.getName());
        File workingDirectory = getWorkingDirectory("samples");
        File out = new File("out.jar");
        File installerFile = new File(workingDirectory, "helloAndFinish.xml");
        CompilerData data = new CompilerData(installerFile.getAbsolutePath(), workingDirectory.getAbsolutePath(), out.getAbsolutePath());
        data.setComprFormat("bzip2");
        data.setComprLevel(2);
        compileInstallJar(data);
        installerContainer = applicationContainer.getComponent(IInstallerContainer.class);
        installerContainer.getComponent(LanguageDialog.class).initLangPack();
        installerFrameFixture = prepareFrameFixture();

        // Hello panel
        installerFrameFixture.requireSize(new Dimension(640, 480));
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        installerFrameFixture.requireVisible();
        // Finish panel
    }


    @Test(dependsOnMethods = "testHelloAndFinishPanels")
    public void testBasicInstall() throws Exception
    {
        compileInstallJar("basicInstall.xml", getWorkingDirectory("samples/basicInstall"));
        GUIInstallData installData = applicationContainer.getComponent(GUIInstallData.class);

        File installPath = prepareInstallation(installData);
        // Lang picker
        clickDefaultLang();

        installerFrameFixture = prepareFrameFixture();
        Thread.sleep(600);
        // Hello panel
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        Thread.sleep(600);
        // Info Panel
        installerFrameFixture.textBox(GuiId.INFO_PANEL_TEXT_AREA.id).requireText("A readme file ...");
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        Thread.sleep(300);
        // Licence Panel
        installerFrameFixture.textBox(GuiId.LICENCE_TEXT_AREA.id).requireText("(Consider it as a licence file ...)");
        installerFrameFixture.radioButton(GuiId.LICENCE_NO_RADIO.id).requireSelected();
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).requireDisabled();
        installerFrameFixture.radioButton(GuiId.LICENCE_YES_RADIO.id).click();
        Thread.sleep(300);
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Target Panel
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        Thread.sleep(300);
        installerFrameFixture.optionPane().requireWarningMessage();
        installerFrameFixture.optionPane().okButton().click();
        // Packs Panel
        Thread.sleep(300);
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Install Panel
        waitAndCheckInstallation(installData, installPath);

        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Finish panel
        installerFrameFixture.button(GuiId.FINISH_PANEL_AUTO_BUTTON.id).click();
        Thread.sleep(800);
        installerFrameFixture.fileChooser(GuiId.FINISH_PANEL_FILE_CHOOSER.id).fileNameTextBox().enterText("auto.xml");
        Thread.sleep(300);
        installerFrameFixture.fileChooser(GuiId.FINISH_PANEL_FILE_CHOOSER.id).approve();
        assertThat(new File(installPath, "auto.xml").exists(), Is.is(true));
    }

    private void clickDefaultLang()
    {
        dialogFrameFixture = prepareDialogFixture();
        dialogFrameFixture.button(GuiId.BUTTON_LANG_OK.id).click();
        // Seems necessary to unlock window
        dialogFrameFixture.cleanUp();
        dialogFrameFixture = null;
    }

    private File prepareInstallation(GUIInstallData installData) throws IOException
    {
        installerContainer = applicationContainer.getComponent(IInstallerContainer.class);
        File installPath = new File(installData.getInstallPath());
        FileUtils.deleteDirectory(installPath);
        assertThat(installPath.exists(), Is.is(false));
        return installPath;
    }


    @Test(dependsOnMethods = "testBasicInstall")
    public void testIzpackInstallation() throws Exception
    {
        compileInstallJar("install.xml", getWorkingDirectory("samples/izpack"));
        GUIInstallData installData = applicationContainer.getComponent(GUIInstallData.class);

        File installPath = prepareInstallation(installData);
        clickDefaultLang();

        installerFrameFixture = prepareFrameFixture();
        // Hello panel
        Thread.sleep(100);
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Chack Panel
        Thread.sleep(100);
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Licence Panel
        installerFrameFixture.radioButton(GuiId.LICENCE_YES_RADIO.id).click();
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Target Panel
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        installerFrameFixture.optionPane().requireWarningMessage();
        installerFrameFixture.optionPane().okButton().click();
        // Packs
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Summary
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Install
        waitAndCheckInstallation(installData, installPath);

        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Shortcut
        // Deselect shortcut creation
        Thread.sleep(400);
        installerFrameFixture.checkBox(GuiId.SHORTCUT_CREATE_CHECK_BOX.id).click();
        installerFrameFixture.button(GuiId.BUTTON_NEXT.id).click();
        // Finish

    }

    private void waitAndCheckInstallation(GUIInstallData installData, File installPath) throws InterruptedException
    {
        while (!installData.isCanClose())
        {
            Thread.sleep(500);
        }
        assertThat(installPath.exists(), Is.is(true));
        UninstallData uninstallData = new UninstallData();
        for (String p : uninstallData.getInstalledFilesList())
        {
            File f = new File(p);
            assertThat(f.exists(), Is.is(true));
        }
    }
}