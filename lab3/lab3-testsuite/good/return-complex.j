.class public return-complex
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

  invokestatic return-complex/main()I
  pop
  return

.end method


.method public static foo(I)Z
  .limit locals 2
  .limit stack 5

  iload_0
  iconst_0
  if_icmpeq L4
  iconst_0
  iconst_0
  ireturn
  goto L1
  L1:
  iconst_1
  istore_1
  iload_0
  invokestatic Runtime/printInt(I)V
  iload_1
  ireturn

.end method

.method public static printBool(Z)V
  .limit locals 1
  .limit stack 5

  iconst_0
  iconst_0
  goto L6
  iload_0
  iconst_0
  iconst_0
  invokestatic Runtime/printInt(I)V
  goto L8
  iconst_1
  invokestatic Runtime/printInt(I)V
  L8:
  L6:
return
  ;; default return
iconst_0
ireturn

.end method

.method public static main()I
  .limit locals 0
  .limit stack 1

  bipush 42
  invokestatic return-complex/foo(I)Z
  invokestatic return-complex/printBool(Z)V
  iconst_0
  invokestatic return-complex/foo(I)Z
  invokestatic return-complex/printBool(Z)V
  iconst_0
  ireturn
  iconst_1
  invokestatic return-complex/printBool(Z)V

  ;; default return
iconst_0
ireturn

.end method
