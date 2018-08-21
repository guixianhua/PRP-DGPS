# PRP-DGPS
Design of Differential GPS Positioning Algorithm and APP Development Based on Smartphone

- $GPGSV
```
GSV message fields
Field Meaning
0 Message ID $GPGSV
1 Total number of messages of this type in this cycle
2 Message number
3 Total number of SVs visible
4 SV PRN number
5 Elevation, in degrees, 90° maximum
6 Azimuth, degrees from True North, 000° through 359°
7 SNR, 00 through 99 dB (null when not tracking)
8-11 Information about second SV, same format as fields 4 through 7
12-15 Information about third SV, same format as fields 4 through 7
16-19 Information about fourth SV, same format as fields 4 through 7
20 The checksum data, always begins with *
```
