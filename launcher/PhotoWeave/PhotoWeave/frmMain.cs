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

namespace PhotoWeave
{
    public partial class frmMain : Form
    {
        public frmMain()
        {
            InitializeComponent();
        }

        private void frmMain_Load(object sender, EventArgs e)
        {
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
                    crashCorrupt();
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
                crashCorrupt();
            }

            Application.Exit();
        }

        private void bgwExtract_DoWork(object sender, DoWorkEventArgs e)
        {
            ZipFile.ExtractToDirectory(@".\package\jre.zip", @".\package");
            File.Delete(@".\package\jre.zip");
        }

        private void crashCorrupt()
        {
            MessageBox.Show("De installatie is corrupt, installeer PhotoWeave opnieuw.", "PhotoWeave", MessageBoxButtons.OK, MessageBoxIcon.Error, MessageBoxDefaultButton.Button1, MessageBoxOptions.DefaultDesktopOnly, false);
            Application.Exit();
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
