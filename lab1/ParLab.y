-- This Happy file was machine-generated by the BNF converter
{
{-# OPTIONS_GHC -fno-warn-incomplete-patterns -fno-warn-overlapping-patterns #-}
module ParLab where
import AbsLab
import LexLab
import ErrM

}

%name pProgram Program
%name pDef Def
%name pListDef ListDef
%name pListArg ListArg
%name pListStm ListStm
%name pArg Arg
%name pIDecl IDecl
%name pListIDecl ListIDecl
%name pDecl Decl
%name pStm Stm
%name pExp16 Exp16
%name pExp15 Exp15
%name pExp14 Exp14
%name pExp13 Exp13
%name pExp12 Exp12
%name pExp11 Exp11
%name pExp10 Exp10
%name pExp9 Exp9
%name pExp8 Exp8
%name pExp4 Exp4
%name pExp3 Exp3
%name pExp2 Exp2
%name pExp1 Exp1
%name pExp Exp
%name pExp5 Exp5
%name pExp6 Exp6
%name pExp7 Exp7
%name pListExp ListExp
%name pListString ListString
%name pListId ListId
%name pType Type
%name pTypes Types
%name pQualifiedConstElem QualifiedConstElem
%name pListTypes ListTypes
%name pQualifiedConstant QualifiedConstant
%name pListQualifiedConstElem ListQualifiedConstElem
-- no lexer declaration
%monad { Err } { thenM } { returnM }
%tokentype {Token}
%token
  '!' { PT _ (TS _ 1) }
  '!=' { PT _ (TS _ 2) }
  '%' { PT _ (TS _ 3) }
  '&' { PT _ (TS _ 4) }
  '&&' { PT _ (TS _ 5) }
  '(' { PT _ (TS _ 6) }
  ')' { PT _ (TS _ 7) }
  '*' { PT _ (TS _ 8) }
  '+' { PT _ (TS _ 9) }
  '++' { PT _ (TS _ 10) }
  '+=' { PT _ (TS _ 11) }
  ',' { PT _ (TS _ 12) }
  '-' { PT _ (TS _ 13) }
  '--' { PT _ (TS _ 14) }
  '-=' { PT _ (TS _ 15) }
  '->' { PT _ (TS _ 16) }
  '.' { PT _ (TS _ 17) }
  '/' { PT _ (TS _ 18) }
  ':' { PT _ (TS _ 19) }
  '::' { PT _ (TS _ 20) }
  ';' { PT _ (TS _ 21) }
  '<' { PT _ (TS _ 22) }
  '<<' { PT _ (TS _ 23) }
  '<=' { PT _ (TS _ 24) }
  '=' { PT _ (TS _ 25) }
  '==' { PT _ (TS _ 26) }
  '>' { PT _ (TS _ 27) }
  '>=' { PT _ (TS _ 28) }
  '>>' { PT _ (TS _ 29) }
  '?' { PT _ (TS _ 30) }
  '[' { PT _ (TS _ 31) }
  ']' { PT _ (TS _ 32) }
  'bool' { PT _ (TS _ 33) }
  'char' { PT _ (TS _ 34) }
  'const' { PT _ (TS _ 35) }
  'do' { PT _ (TS _ 36) }
  'double' { PT _ (TS _ 37) }
  'else' { PT _ (TS _ 38) }
  'false' { PT _ (TS _ 39) }
  'float' { PT _ (TS _ 40) }
  'for' { PT _ (TS _ 41) }
  'if' { PT _ (TS _ 42) }
  'int' { PT _ (TS _ 43) }
  'return' { PT _ (TS _ 44) }
  'throw' { PT _ (TS _ 45) }
  'true' { PT _ (TS _ 46) }
  'typedef' { PT _ (TS _ 47) }
  'using' { PT _ (TS _ 48) }
  'void' { PT _ (TS _ 49) }
  'while' { PT _ (TS _ 50) }
  '{' { PT _ (TS _ 51) }
  '||' { PT _ (TS _ 52) }
  '}' { PT _ (TS _ 53) }

L_integ  { PT _ (TI $$) }
L_doubl  { PT _ (TD $$) }
L_charac { PT _ (TC $$) }
L_quoted { PT _ (TL $$) }
L_Id { PT _ (T_Id $$) }


%%

Integer :: { Integer } : L_integ  { (read ( $1)) :: Integer }
Double  :: { Double }  : L_doubl  { (read ( $1)) :: Double }
Char    :: { Char }    : L_charac { (read ( $1)) :: Char }
String  :: { String }  : L_quoted {  $1 }
Id    :: { Id} : L_Id { Id ($1)}

Program :: { Program }
Program : ListDef { AbsLab.PDefs (reverse $1) }
Def :: { Def }
Def : Type Id '(' ListArg ')' '{' ListStm '}' { AbsLab.DFun $1 $2 $4 (reverse $7) }
    | Type Id '(' ListArg ')' ';' { AbsLab.DFunEmpty $1 $2 $4 }
    | 'using' QualifiedConstant ';' { AbsLab.DUsing $2 }
    | 'typedef' Type Id ';' { AbsLab.DTypeDef $2 $3 }
    | Decl ';' { AbsLab.DVariable $1 }
ListDef :: { [Def] }
ListDef : {- empty -} { [] } | ListDef Def { flip (:) $1 $2 }
ListArg :: { [Arg] }
ListArg : {- empty -} { [] }
        | Arg { (:[]) $1 }
        | Arg ',' ListArg { (:) $1 $3 }
ListStm :: { [Stm] }
ListStm : {- empty -} { [] } | ListStm Stm { flip (:) $1 $2 }
Arg :: { Arg }
Arg : Type { AbsLab.AFunc $1 }
    | Type Id { AbsLab.ADecl $1 $2 }
    | 'const' Type Id { AbsLab.AConst $2 $3 }
    | 'const' Type { AbsLab.AConstType $2 }
    | 'const' Type Id '=' Exp { AbsLab.AInitConst $2 $3 $5 }
    | Type Id '=' Exp { AbsLab.AInit $1 $2 $4 }
IDecl :: { IDecl }
IDecl : Id { AbsLab.DeclId $1 }
      | Id '=' Exp { AbsLab.IDeclAss $1 $3 }
ListIDecl :: { [IDecl] }
ListIDecl : IDecl { (:[]) $1 } | IDecl ',' ListIDecl { (:) $1 $3 }
Decl :: { Decl }
Decl : 'const' Type ListIDecl { AbsLab.ConstDecl $2 $3 }
     | Type ListIDecl { AbsLab.NoConstDecl $1 $2 }
Stm :: { Stm }
Stm : 'typedef' Type Id ';' { AbsLab.STypeDef $2 $3 }
    | Exp ';' { AbsLab.SExp $1 }
    | Decl ';' { AbsLab.SDecl $1 }
    | 'return' Exp ';' { AbsLab.SReturn $2 }
    | 'while' '(' Exp ')' Stm { AbsLab.SWhile $3 $5 }
    | 'do' Stm 'while' '(' Exp ')' ';' { AbsLab.SDoWhile $2 $5 }
    | '{' ListStm '}' { AbsLab.SBlock (reverse $2) }
    | 'if' '(' Exp ')' Stm 'else' Stm { AbsLab.SIfElse $3 $5 $7 }
    | 'if' '(' Exp ')' Stm { AbsLab.SIf $3 $5 }
    | 'for' '(' Decl ';' Exp ';' Exp ')' Stm { AbsLab.SFor $3 $5 $7 $9 }
    | 'const' Type Id '=' Exp ';' { AbsLab.SFuncConst $2 $3 $5 }
Exp16 :: { Exp }
Exp16 : QualifiedConstant { AbsLab.EConst $1 }
      | ListString { AbsLab.EString $1 }
      | Integer { AbsLab.EInt $1 }
      | Double { AbsLab.EDouble $1 }
      | 'true' { AbsLab.ETrue }
      | Char { AbsLab.EChar $1 }
      | 'false' { AbsLab.EFalse }
      | '(' Exp ')' { $2 }
Exp15 :: { Exp }
Exp15 : Exp15 '[' Exp ']' { AbsLab.EIndex $1 $3 }
      | Exp15 '(' ListExp ')' { AbsLab.EApp $1 $3 }
      | Exp16 { $1 }
Exp14 :: { Exp }
Exp14 : Exp14 '.' Exp15 { AbsLab.Emem $1 $3 }
      | Exp14 '++' { AbsLab.EPIncr $1 }
      | Exp14 '--' { AbsLab.EPDecr $1 }
      | Exp14 '->' Exp15 { AbsLab.EPointer $1 $3 }
      | Exp15 { $1 }
Exp13 :: { Exp }
Exp13 : '!' Exp13 { AbsLab.ENot $2 }
      | '*' Exp13 { AbsLab.EDRef $2 }
      | '++' Exp13 { AbsLab.EIncr $2 }
      | '--' Exp13 { AbsLab.EDecr $2 }
      | Exp14 { $1 }
Exp12 :: { Exp }
Exp12 : Exp12 '*' Exp13 { AbsLab.ETimes $1 $3 }
      | Exp12 '/' Exp13 { AbsLab.EDiv $1 $3 }
      | Exp12 '%' Exp13 { AbsLab.EMod $1 $3 }
      | Exp13 { $1 }
Exp11 :: { Exp }
Exp11 : Exp11 '+' Exp12 { AbsLab.EPlus $1 $3 }
      | Exp11 '-' Exp12 { AbsLab.EMinus $1 $3 }
      | Exp12 { $1 }
Exp10 :: { Exp }
Exp10 : Exp10 '<<' Exp11 { AbsLab.ELshift $1 $3 }
      | Exp10 '>>' Exp11 { AbsLab.ERshift $1 $3 }
      | Exp11 { $1 }
Exp9 :: { Exp }
Exp9 : Exp9 '<' Exp10 { AbsLab.ELt $1 $3 }
     | Exp9 '>' Exp10 { AbsLab.EGt $1 $3 }
     | Exp9 '<=' Exp10 { AbsLab.ELtEq $1 $3 }
     | Exp9 '>=' Exp10 { AbsLab.EGtWq $1 $3 }
     | Exp10 { $1 }
Exp8 :: { Exp }
Exp8 : Exp8 '==' Exp9 { AbsLab.EEq $1 $3 }
     | Exp8 '!=' Exp9 { AbsLab.ENEq $1 $3 }
     | Exp9 { $1 }
Exp4 :: { Exp }
Exp4 : Exp4 '&&' Exp5 { AbsLab.EAnd $1 $3 } | Exp5 { $1 }
Exp3 :: { Exp }
Exp3 : Exp3 '||' Exp4 { AbsLab.EOr $1 $3 } | Exp4 { $1 }
Exp2 :: { Exp }
Exp2 : Exp3 '=' Exp2 { AbsLab.EAss $1 $3 }
     | Exp3 '-=' Exp2 { AbsLab.EaDec $1 $3 }
     | Exp3 '+=' Exp2 { AbsLab.EaInc $1 $3 }
     | Exp2 '?' Exp3 ':' Exp3 { AbsLab.EIfElse $1 $3 $5 }
     | Exp3 { $1 }
Exp1 :: { Exp }
Exp1 : 'throw' Exp1 { AbsLab.EThrow $2 } | Exp2 { $1 }
Exp :: { Exp }
Exp : Exp1 { $1 }
Exp5 :: { Exp }
Exp5 : Exp6 { $1 }
Exp6 :: { Exp }
Exp6 : Exp7 { $1 }
Exp7 :: { Exp }
Exp7 : Exp8 { $1 }
ListExp :: { [Exp] }
ListExp : {- empty -} { [] }
        | Exp { (:[]) $1 }
        | Exp ',' ListExp { (:) $1 $3 }
ListString :: { [String] }
ListString : String { (:[]) $1 } | String ListString { (:) $1 $2 }
ListId :: { [Id] }
ListId : Id { (:[]) $1 } | Id ',' ListId { (:) $1 $3 }
Type :: { Type }
Type : 'bool' { AbsLab.Tbool }
     | 'double' { AbsLab.Tdouble }
     | 'float' { AbsLab.TFloat }
     | 'int' { AbsLab.Tint }
     | 'void' { AbsLab.Tvoid }
     | 'char' { AbsLab.TChar }
     | QualifiedConstant { AbsLab.TList $1 }
     | Type '&' { AbsLab.TRef $1 }
Types :: { Types }
Types : Type { AbsLab.TypeListElem $1 }
QualifiedConstElem :: { QualifiedConstElem }
QualifiedConstElem : Id { AbsLab.QualifiedConstElemId $1 }
                   | Id '<' ListTypes '>' { AbsLab.QualifiedConstElemInstance $1 $3 }
ListTypes :: { [Types] }
ListTypes : Types { (:[]) $1 } | Types ',' ListTypes { (:) $1 $3 }
QualifiedConstant :: { QualifiedConstant }
QualifiedConstant : ListQualifiedConstElem { AbsLab.QualifiedConstElems $1 }
ListQualifiedConstElem :: { [QualifiedConstElem] }
ListQualifiedConstElem : QualifiedConstElem { (:[]) $1 }
                       | QualifiedConstElem '::' ListQualifiedConstElem { (:) $1 $3 }
{

returnM :: a -> Err a
returnM = return

thenM :: Err a -> (a -> Err b) -> Err b
thenM = (>>=)

happyError :: [Token] -> Err a
happyError ts =
  Bad $ "syntax error at " ++ tokenPos ts ++ 
  case ts of
    [] -> []
    [Err _] -> " due to lexer error"
    _ -> " before " ++ unwords (map (id . prToken) (take 4 ts))

myLexer = tokens
}

