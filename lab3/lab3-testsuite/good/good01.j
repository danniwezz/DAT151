.class public good01
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

  invokestatic good01/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 1

  iconst_1
  invokestatic Runtime/printInt(I)V

  ;; default return
iconst_0
ireturn

.end method
