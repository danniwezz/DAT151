.class public return-in-if-right
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

  invokestatic return-in-if-right/main()I
  pop
  return

.end method


.method public static g()I
  .limit locals 0
  .limit stack 3

  iconst_0
  iconst_0
  bipush 12
  ireturn
  goto L1
  bipush 11
  ireturn
  L1:
  bipush 10
  ireturn

.end method

.method public static main()I
  .limit locals 0
  .limit stack 1

  invokestatic return-in-if-right/g()I
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
