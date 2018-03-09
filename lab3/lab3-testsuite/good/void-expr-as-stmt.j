.class public void-expr-as-stmt
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

  invokestatic void-expr-as-stmt/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 1

  invokestatic void-expr-as-stmt/foo()V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method

.method public static foo()V
  .limit locals 0
  .limit stack 1

  iconst_1
  invokestatic Runtime/printInt(I)V
return
  ;; default return
iconst_0
ireturn

.end method
