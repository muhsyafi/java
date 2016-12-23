package com.mySwingexamples;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SwingUI extends JPanel
{
                public void run() {
                                 final JFrame f = new JFrame("Simple hello Example");
                                final JTextField fName = new JTextField();
                                JButton submit = new JButton("Enter");
                                 JLabel lab = new JLabel("Enter Name", JLabel.RIGHT);
                                 fName.setColumns(10); 
                                 lab.setLabelFor(fName);
                                  
                                 submit.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                                 fName.getText();
                                                 JOptionPane.showMessageDialog(f, "Hello "+ fName.getText());
                                     }
                                   });
                               
                                 JPanel form = new JPanel();
                                 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                 f.getContentPane().add(form, BorderLayout.NORTH);
                                 JPanel p = new JPanel();
                                 p.add(lab);
                                 p.add(fName);
                                 p.add(submit);
                                 f.getContentPane().add(p, BorderLayout.SOUTH);
                                 f.pack();
                                 f.setVisible(true);
                }
}