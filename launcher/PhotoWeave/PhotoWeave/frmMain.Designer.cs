namespace PhotoWeave
{
    partial class frmMain
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.pbrExtraction = new System.Windows.Forms.ProgressBar();
            this.lblPatience = new System.Windows.Forms.Label();
            this.bgwExtract = new System.ComponentModel.BackgroundWorker();
            this.tmrUpdateDirsize = new System.Windows.Forms.Timer(this.components);
            this.lblProgress = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // pbrExtraction
            // 
            this.pbrExtraction.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.pbrExtraction.ForeColor = System.Drawing.Color.GreenYellow;
            this.pbrExtraction.Location = new System.Drawing.Point(16, 118);
            this.pbrExtraction.MarqueeAnimationSpeed = 35;
            this.pbrExtraction.Name = "pbrExtraction";
            this.pbrExtraction.Size = new System.Drawing.Size(391, 33);
            this.pbrExtraction.Style = System.Windows.Forms.ProgressBarStyle.Marquee;
            this.pbrExtraction.TabIndex = 0;
            this.pbrExtraction.Value = 100;
            // 
            // lblPatience
            // 
            this.lblPatience.AutoSize = true;
            this.lblPatience.Dock = System.Windows.Forms.DockStyle.Top;
            this.lblPatience.Location = new System.Drawing.Point(16, 16);
            this.lblPatience.Name = "lblPatience";
            this.lblPatience.Size = new System.Drawing.Size(237, 17);
            this.lblPatience.TabIndex = 1;
            this.lblPatience.Text = "Even geduld, Java wordt uitgepakt...";
            // 
            // bgwExtract
            // 
            this.bgwExtract.DoWork += new System.ComponentModel.DoWorkEventHandler(this.bgwExtract_DoWork);
            // 
            // tmrUpdateDirsize
            // 
            this.tmrUpdateDirsize.Interval = 1000;
            this.tmrUpdateDirsize.Tick += new System.EventHandler(this.tmrUpdateDirsize_Tick);
            // 
            // lblProgress
            // 
            this.lblProgress.AutoSize = true;
            this.lblProgress.ForeColor = System.Drawing.Color.Blue;
            this.lblProgress.Location = new System.Drawing.Point(19, 55);
            this.lblProgress.Name = "lblProgress";
            this.lblProgress.Size = new System.Drawing.Size(0, 17);
            this.lblProgress.TabIndex = 2;
            // 
            // frmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(423, 167);
            this.ControlBox = false;
            this.Controls.Add(this.lblProgress);
            this.Controls.Add(this.lblPatience);
            this.Controls.Add(this.pbrExtraction);
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "frmMain";
            this.Padding = new System.Windows.Forms.Padding(16);
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "PhotoWeave launcher";
            this.TopMost = true;
            this.Load += new System.EventHandler(this.frmMain_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ProgressBar pbrExtraction;
        private System.Windows.Forms.Label lblPatience;
        private System.ComponentModel.BackgroundWorker bgwExtract;
        private System.Windows.Forms.Timer tmrUpdateDirsize;
        private System.Windows.Forms.Label lblProgress;
    }
}

