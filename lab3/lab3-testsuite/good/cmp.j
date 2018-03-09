.class public cmp
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

  invokestatic cmp/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 7

  bipush 40
  bipush 40
  if_icmpeq L2
  invokestatic cmp/printBool(Z)V
  bipush 40
  bipush 40
  if_icmpne L5
  invokestatic cmp/printBool(Z)V
  iconst_3
  iconst_4
  if_icmpgt L8
  invokestatic cmp/printBool(Z)V
  iconst_4
  iconst_3
  if_icmplt L11
  invokestatic cmp/printBool(Z)V
  ldc 400
  ldc 300
  if_icmpge L14
  invokestatic cmp/printBool(Z)V
  ldc 400
  ldc 300
  if_icmple L17
  invokestatic cmp/printBool(Z)V

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
  goto L19
  iconst_0
  invokestatic Runtime/printInt(I)V
  L19:
return
  ;; default return
iconst_0
ireturn

.end method
