using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO.Compression;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;
using System.Net;

namespace PhotoWeave
{
    public partial class frmMain : Form
    {
        public frmMain()
        {
            InitializeComponent();
        }

        private void checkLatestVersion()
        {
            using (WebClient wc = new WebClient())
            {
                String latestVersionUrl = wc.DownloadString(Properties.Settings.Default.LatestVersionUrl);

                Uri uri = new Uri(latestVersionUrl);
                string newFilename = Path.GetFileName(uri.LocalPath);

                string[] packageFiles = Directory.GetFiles(@".\package", "*.jar");
                string currentFilename = "";

                if (packageFiles.Length > 0)
                {
                    currentFilename = Path.GetFileName(packageFiles[0]);
                }

                if (!newFilename.Equals(currentFilename))
                {
                    DialogResult dlr = DialogResult.Yes;

                    if (!currentFilename.Equals(""))
                    {
                        dlr = MessageBox.Show("Er is een nieuwe versie van PhotoWeave beschikbaar! Downloaden?" + Environment.NewLine + "Huidig: " + currentFilename + ", nieuw: " + newFilename, "PhotoWeave", MessageBoxButtons.YesNo);
                    } else
                    {
                        MessageBox.Show("PhotoWeave moet nog gedownload worden. PhotoWeave zal starten eenmaal het gedownload is, klik op OK.");
                    }

                    if (dlr == DialogResult.Yes)
                    {
                        //Delete any old installations
                        DirectoryInfo di = new DirectoryInfo(@".\package");

                        foreach (FileInfo file in di.GetFiles())
                        {
                            if (file.Extension.Equals(".jar")) file.Delete();
                        }

                        new DownloadForm(latestVersionUrl, newFilename).ShowDialog();
                    }
                }
            }
        }

        private void frmMain_Load(object sender, EventArgs e)
        {
            if (!Directory.Exists(@".\package")) crashCorrupt("Package map bestaat niet.");
            if (!Properties.Settings.Default.SkipUpdating) checkLatestVersion();

            if (File.Exists(@".\package\jre\bin\java.exe"))
            {
                LaunchApplication(sender, null);
            }
            else
            {
                if (File.Exists(@".\package\jre.zip"))
                {
                    MessageBox.Show("Dit lijkt de eerste keer te zijn dat je PhotoWeave start. De applicatie moet eerst uitgepakt worden. Dit kan even duren.", "PhotoWeave", MessageBoxButtons.OK, MessageBoxIcon.Information, MessageBoxDefaultButton.Button1, MessageBoxOptions.DefaultDesktopOnly, false);
                    bgwExtract.RunWorkerCompleted += LaunchApplication;
                    bgwExtract.RunWorkerAsync();
                    tmrUpdateDirsize.Start();
                }
                else
                {
                    crashCorrupt("jre.zip niet gevonden in package map");
                }
            }
        }

        private void LaunchApplication(object sender, RunWorkerCompletedEventArgs e)
        {
            string[] files = Directory.GetFiles(@".\package", "*.jar");

            if (files.Length > 0)
            {
                ProcessStartInfo startInfo = new ProcessStartInfo(@".\package\jre\bin\java.exe", "-jar " + files[0]);
                startInfo.RedirectStandardOutput = true;
                startInfo.RedirectStandardError = true;
                startInfo.UseShellExecute = false;
                startInfo.CreateNoWindow = true;

                Process process = Process.Start(startInfo); //redirect outputstream to log
                this.Hide();
                process.WaitForExit();
                String output = process.StandardOutput.ReadToEnd();
                File.WriteAllText(@".\log.txt", output);
            }
            else
            {
                crashCorrupt("Geen .jar files in package map om te starten");
            }

            Environment.Exit(0);
        }

        private void bgwExtract_DoWork(object sender, DoWorkEventArgs e)
        {
            ZipFile.ExtractToDirectory(@".\package\jre.zip", @".\package");
            File.Delete(@".\package\jre.zip");
        }

        private void crashCorrupt(string reason)
        {
            MessageBox.Show("De installatie is corrupt, installeer PhotoWeave opnieuw." + Environment.NewLine + "Reden: " + reason, "PhotoWeave", MessageBoxButtons.OK, MessageBoxIcon.Error, MessageBoxDefaultButton.Button1, MessageBoxOptions.DefaultDesktopOnly, false);
            Environment.Exit(0);
        }

        private long DirSize(DirectoryInfo d)
        {
            long size = 0;
            // Add file sizes.
            FileInfo[] fis = d.GetFiles();
            foreach (FileInfo fi in fis)
            {
                size += fi.Length;
            }
            // Add subdirectory sizes.
            DirectoryInfo[] dis = d.GetDirectories();
            foreach (DirectoryInfo di in dis)
            {
                size += DirSize(di);
            }
            return size;
        }

        private void tmrUpdateDirsize_Tick(object sender, EventArgs e)
        {
            double dirSize = DirSize(new DirectoryInfo(@".\package\jre")) / 1024.0 / 1024.0; //MB
            dirSize = Math.Round(dirSize, 2);

            lblProgress.Text = dirSize.ToString() + " MB uitgepakt";
        }
    }
}
