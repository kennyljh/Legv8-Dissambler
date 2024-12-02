    ORR X0, XZR, XZR
    ORRI X1, XZR, #10
    BL Label0
    ORR X0, XZR, XZR
    ORRI X1, XZR, #10
    BL Label1
    HALT
Label0:
    ORR X2, XZR, XZR
    B Label2
Label3:
    SUB X3, X1, X2
    SUBI X3, X3, #1
    LSL X4, X2, #3
    ADD X4, X0, X4
    STUR X3, [X4, #0]
    ADDI X2, X2, #1
Label2:
    SUBS XZR, X2, X1
    B.LT Label3
    BR LR
Label1:
    SUBI SP, SP, #8
    STUR LR, [SP, #0]
    SUBI X2, X1, #1
    ORR X1, XZR, XZR
    BL Label4
    LDUR LR, [SP, #0]
    ADDI SP, SP, #8
    BR LR
Label4:
    SUBI SP, SP, #40
    STUR X19, [SP, #0]
    STUR X20, [SP, #8]
    STUR X21, [SP, #16]
    STUR X22, [SP, #24]
    STUR LR, [SP, #32]
    ORR X19, X0, XZR
    ORR X20, X1, XZR
    ORR X21, X2, XZR
    SUBS XZR, X20, X21
    B.GE Label5
    BL Label6
    ORR X22, X0, XZR
    ORR X0, X19, XZR
    ORR X1, X20, XZR
    SUBI X2, X22, #1
    BL Label4
    ORR X0, X19, XZR
    ADDI X1, X22, #1
    ORR X2, X21, XZR
    BL Label4
Label5:
    LDUR X19, [SP, #0]
    LDUR X20, [SP, #8]
    LDUR X21, [SP, #16]
    LDUR X22, [SP, #24]
    LDUR LR, [SP, #32]
    ADDI SP, SP, #40
    BR LR
Label6:
    SUBI SP, SP, #56
    STUR X19, [SP, #0]
    STUR X20, [SP, #8]
    STUR X21, [SP, #16]
    STUR X22, [SP, #24]
    STUR X23, [SP, #32]
    STUR X24, [SP, #40]
    STUR LR, [SP, #48]
    ORR X19, X0, XZR
    ORR X20, X1, XZR
    ORR X21, X2, XZR
    ADDI X22, X20, #1
    ORR X23, X21, XZR
    LSL X3, X20, #3
    ADD X3, X19, X3
    LDUR X24, [X3, #0]
    B Label7
Label7:
    ORR X0, X19, XZR
    ORR X1, X22, XZR
    ORR X2, X21, XZR
    ORR X3, X24, XZR
    BL Label8
    ORR X22, X0, XZR
    ORR X0, X19, XZR
    ORR X1, X23, XZR
    ORR X2, X20, XZR
    ORR X3, X24, XZR
    BL Label9
    ORR X23, X0, XZR
    SUBS XZR, X22, X23
    B.GE Label10
    ORR X0, X19, XZR
    ORR X1, X22, XZR
    ORR X2, X23, XZR
    BL Label11
    B Label7
Label10:
    SUBS XZR, X23, X20
    B.EQ Label12
    ORR X0, X19, XZR
    ORR X1, X23, XZR
    ORR X2, X20, XZR
    BL Label11
Label12:
    ORR X0, X23, XZR
    LDUR X19, [SP, #0]
    LDUR X20, [SP, #8]
    LDUR X21, [SP, #16]
    LDUR X22, [SP, #24]
    LDUR X23, [SP, #32]
    LDUR X24, [SP, #40]
    LDUR LR, [SP, #48]
    ADDI SP, SP, #56
    BR LR
Label8:
    B Label13
Label15:
    ADDI X1, X1, #1
Label13:
    SUBS XZR, X1, X2
    B.GT Label14
    LSL X4, X1, #3
    ADD X4, X0, X4
    LDUR X4, [X4, #0]
    SUBS XZR, X4, X3
    B.LT Label15
Label14:
    ORR X0, X1, XZR
    BR LR
Label9:
    B Label16
Label18:
    SUBI X1, X1, #1
Label16:
    SUBS XZR, X1, X2
    B.LE Label17
    LSL X4, X1, #3
    ADD X4, X0, X4
    LDUR X4, [X4, #0]
    SUBS XZR, X4, X3
    B.GE Label18
Label17:
    ORR X0, X1, XZR
    BR LR
Label11:
    LSL X4, X1, #3
    ADD X4, X0, X4
    LDUR X3, [X4, #0]
    LSL X5, X2, #3
    ADD X5, X0, X5
    LDUR X6, [X5, #0]
    STUR X6, [X4, #0]
    STUR X3, [X5, #0]
    BR LR
