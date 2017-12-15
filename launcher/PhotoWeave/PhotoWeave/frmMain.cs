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

                Process.Start(startInfo);
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
    }
}
