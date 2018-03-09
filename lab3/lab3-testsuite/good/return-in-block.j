.class public return-in-block
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

  invokestatic return-in-block/main()I
  pop
  return

.end method


.method public static niam()I
  .limit locals 0
  .limit stack 1

  bipush 99
  ireturn
  iconst_1
  invokestatic Runtime/printInt(I)V

.end method

.method public static main()I
  .limit locals 0
  .limit stack 1

  invokestatic return-in-block/niam()I
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
