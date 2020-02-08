# 2020 FIRST INFINITE RECHARGE

## Controls

![driver](docs/2020-Driver-Controls.PNG)
![game-controls](docs/2020-Game-Controls.PNG)

## Talons

Subsystem    | Type | Talon       | ID | PDP
------------ | ---- | ----------- | -- | ---
Drive        | SRX  | azimuth     | 0  | 6
Drive        | SRX  | azimuth     | 1  | 4
Drive        | SRX  | azimuth     | 2  | 7
Drive        | SRX  | azimuth     | 3  | 5
Drive        | FX   | drive       | 10 | 2
Drive        | FX   | drive       | 11 | 0
Drive        | FX   | drive       | 12 | 3
Drive        | FX   | drive       | 13 | 1
Intake       | FX   | intake      | 20 | 15
Magazine     | SRX  | magazine    | 30 | 9
Shooter      | FX   | leftMaster  | 40 | 12
Shooter      | FX   | rightSlave  | 41 | 13
Shooter      | SRX  | turret      | 42 | 10
Shooter      | SRX  | hood        | 43 | 11
Climb        | SRX  | climb       | 50 | 14

* Intake beam break routed to reverse limit switch on magazine
* Magazine beam break routed to forward limit switch on hood
