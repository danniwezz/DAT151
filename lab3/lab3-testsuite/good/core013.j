.class public core013
.super java/lang/Object

.method public <init>()V
  .limit locals 1

  aload_0
  invokespecial java/lang/Object/<init>()V
  return

.end method

.method public static main([Ljava/lang/String;)V
  .limit locals 1
  .limit stack  1

  invokestatic core013/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 19

  iconst_0
  iconst_1
  isub
  invokestatic core013/test(I)Z
  ifeq L0
  iconst_0
  invokestatic core013/test(I)Z
  iconst_1
  goto L1
  L0:
  iconst_0
  L1:
  invokestatic core013/printBool(Z)V
  iconst_0
  iconst_2
  isub
  invokestatic core013/test(I)Z
  ifeq L2
  iconst_1
  invokestatic core013/test(I)Z
  iconst_0
  goto L3
  L2:
  iconst_1
  L3:
  invokestatic core013/printBool(Z)V
  iconst_3
  invokestatic core013/test(I)Z
  ifeq L6
  iconst_0
  iconst_5
  isub
  invokestatic core013/test(I)Z
  iconst_1
  goto L7
  L6:
  iconst_0
  L7:
  ifeq L4
  iconst_1
  iconst_1
  goto L5
  L4:
  iconst_0
  L5:
  invokestatic core013/printBool(Z)V
  iconst_3
  invokestatic core013/test(I)Z
  ifeq L8
  iconst_0
  iconst_5
  isub
  invokestatic core013/test(I)Z
  ifeq L10
  iconst_1
  iconst_1
  goto L11
  L10:
  iconst_0
  L11:
  iconst_0
  goto L9
  L8:
  iconst_1
  L9:
  invokestatic core013/printBool(Z)V
  iconst_1
  invokestatic core013/printBool(Z)V
  iconst_0
  invokestatic core013/printBool(Z)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method

.method public static printBool(Z)V
  .limit locals 1
  .limit stack 3

  iload_0
  iconst_0
  iconst_1
  invokestatic Runtime/printInt(I)V
  goto L13
  iconst_0
  invokestatic Runtime/printInt(I)V
  L13:
return
  ;; default return
iconst_0
ireturn

.end method

.method public static test(I)Z
  .limit locals 1
  .limit stack 2

  iload_0
  iconst_0
  if_icmpgt L16
  ireturn

.end method
