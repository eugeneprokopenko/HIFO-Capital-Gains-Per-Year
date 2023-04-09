# HIFO-Capital-Gains-Per-Year
Calculate capital gains of a single stock

This program reads stock purchases and sales from two CSV files, "purchases.csv" and "sales.csv". The term "purchases" includes the receipt of dividends.  It then calculates the capital gains/loss per year using the HIFO (highest-in, first-out) method. If the total sold stock units are more than the total purchased stock units, the program prints a message and terminates. The program uses a PriorityQueue to maintain the order of purchases by price in descending order, and it processes each sale by selling the highest-priced stock first. The capital gains/loss per year is stored in a HashMap and printed at the end.
