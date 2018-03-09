.class public bind-function-arguments-lazily
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

  invokestatic bind-function-arguments-lazily/main()I
  pop
  return

.end method


.method public static snd(II)I
  .limit locals 2
  .limit stack 2

  iload_1
  ireturn

.end method

.method public static main()I
  .limit locals 2
  .limit stack 2

  iconst_0
  istore_0
  iconst_1
  iload_0
  invokestatic bind-function-arguments-lazily/snd(II)I
  istore_1
  iload_1
  invokestatic Runtime/printInt(I)V
  iload_1
  ireturn

  ;; default return
iconst_0
ireturn

.end method
