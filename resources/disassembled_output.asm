Label1:
    ADD X1, X2, X3
    ADDI X4, X5, #987
    SUB X6, X7, X8
    SUBI X9, X10, #2111
    AND X11, X12, X13
    ANDI X14, X15, #654
    EORI X19, X20, #2222
    ORR X21, X22, X23
    ORRI X24, X25, #321
    SUBS X26, X27, SP
    SUBIS FP, LR, #3333
    B.EQ Label0
    B.NE Label1
    B.HS Label0
    B.LO Label0
    B.MI Label0
    B.PL Label0
    B.VS Label0
    B.VC Label0
    B.HI Label0
    B.LS Label0
    B.GE Label0
    B.LT Label0
    B.GT Label0
    B.LE Label0
    BL Label2
    B Label0
    CBNZ X11, Label0
    CBZ X0, Label0
    BR X15
    LDUR X1, [X2, #-256]
    STUR X3, [X4, #255]
    LSL X6, X7, #31
    LSR X8, X9, #1
    MUL X10, X11, X12
    PRNT X0
    PRNL
    DUMP
    HALT
Label0:
    EOR IP0, IP1, X18
Label2:
