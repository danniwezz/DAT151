.class public scopes
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

  invokestatic scopes/main()I
  pop
  return

.end method


.method public static f()I
  .limit locals 2
  .limit stack 4

  iconst_2
  istore_0
  iload_0
  iconst_3
  if_icmplt L4
  iconst_0
  iconst_3
  istore_1
  iload_1
  ireturn
  goto L1
  L1:
  iload_0
  ireturn

.end method

.method public static main()I
  .limit locals 1
  .limit stack 1

  iconst_1
  istore_0
  iload_0
  invokestatic Runtime/printInt(I)V
  invokestatic scopes/f()I
  invokestatic Runtime/printInt(I)V
  iload_0
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
