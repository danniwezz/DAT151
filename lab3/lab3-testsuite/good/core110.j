.class public core110
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

  invokestatic core110/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 1

  bipush 24
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
