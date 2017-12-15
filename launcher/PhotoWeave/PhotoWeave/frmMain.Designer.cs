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
            this.pbrExtraction = new System.Windows.Forms.ProgressBar();
            this.lblPatience = new System.Windows.Forms.Label();
            this.bgwExtract = new System.ComponentModel.BackgroundWorker();
            this.SuspendLayout();
            // 
            // pbrExtraction
            // 
            this.pbrExtraction.ForeColor = System.Drawing.Color.GreenYellow;
            this.pbrExtraction.Location = new System.Drawing.Point(21, 29);
            this.pbrExtraction.MarqueeAnimationSpeed = 35;
            this.pbrExtraction.Name = "pbrExtraction";
            this.pbrExtraction.Size = new System.Drawing.Size(375, 33);
            this.pbrExtraction.Style = System.Windows.Forms.ProgressBarStyle.Marquee;
            this.pbrExtraction.TabIndex = 0;
            this.pbrExtraction.Value = 100;
            // 
            // lblPatience
            // 
            this.lblPatience.AutoSize = true;
            this.lblPatience.Location = new System.Drawing.Point(18, 9);
            this.lblPatience.Name = "lblPatience";
            this.lblPatience.Size = new System.Drawing.Size(99, 17);
            this.lblPatience.TabIndex = 1;
            this.lblPatience.Text = "Even geduld...";
            // 
            // bgwExtract
            // 
            this.bgwExtract.DoWork += new System.ComponentModel.DoWorkEventHandler(this.bgwExtract_DoWork);
            // 
            // frmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(423, 133);
            this.ControlBox = false;
            this.Controls.Add(this.lblPatience);
            this.Controls.Add(this.pbrExtraction);
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "frmMain";
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
    }
}

