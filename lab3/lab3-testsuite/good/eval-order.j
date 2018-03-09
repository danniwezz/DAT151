.class public eval-order
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

  invokestatic eval-order/main()I
  pop
  return

.end method


.method public static order(II)I
  .limit locals 2
  .limit stack 2

  iconst_0
  ireturn

.end method

.method public static printIntInt(I)I
  .limit locals 1
  .limit stack 1

  iload_0
  invokestatic Runtime/printInt(I)V
  iload_0
  ireturn

.end method

.method public static main()I
  .limit locals 0
  .limit stack 5

  iconst_0
  invokestatic eval-order/printIntInt(I)I
  iconst_1
  invokestatic eval-order/printIntInt(I)I
  invokestatic eval-order/order(II)I
  iconst_2
  invokestatic eval-order/printIntInt(I)I
  iconst_3
  invokestatic eval-order/printIntInt(I)I
  iadd
  iconst_4
  invokestatic eval-order/printIntInt(I)I
  iconst_5
  invokestatic eval-order/printIntInt(I)I
  if_icmpeq L2
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
