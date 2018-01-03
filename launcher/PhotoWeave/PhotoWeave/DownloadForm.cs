using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace PhotoWeave
{
    public partial class DownloadForm : Form
    {
        private string url;
        private string newFilename;

        public DownloadForm(string url, string newFilename)
        {
            InitializeComponent();

            this.url = url;
            this.newFilename = newFilename;
        }

        private void DownloadForm_Load(object sender, EventArgs e)
        {
            using (WebClient wc = new WebClient())
            {
                wc.DownloadFileCompleted += this.finishedDownload;
                wc.DownloadProgressChanged += this.updateProgress;
                wc.DownloadFileAsync(new Uri(this.url), Path.Combine(@".\package", this.newFilename));
            }
        }

        private void updateProgress(Object sender, DownloadProgressChangedEventArgs args)
        {
            this.pbrProgress.Value = args.ProgressPercentage;
        }

        private void finishedDownload(Object sender, AsyncCompletedEventArgs args)
        {
            this.Close();
        }
    }
}
