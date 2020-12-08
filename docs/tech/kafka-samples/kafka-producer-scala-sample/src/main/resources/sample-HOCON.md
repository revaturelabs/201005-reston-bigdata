### "Human-Optimized Config Object Notation"
(from the docs available [here](https://github.com/lightbend/config))

Examples of HOCON
All of these are valid HOCON.

Start with valid JSON:

{
    "foo" : {
        "bar" : 10,
        "baz" : 12
    }
}
Drop root braces:

"foo" : {
    "bar" : 10,
    "baz" : 12
}
Drop quotes:

foo : {
    bar : 10,
    baz : 12
}
Use = and omit it before {:

foo {
    bar = 10,
    baz = 12
}
Remove commas:

foo {
    bar = 10
    baz = 12
}
Use dotted notation for unquoted keys:

foo.bar=10
foo.baz=12
Put the dotted-notation fields on a single line:

foo.bar=10, foo.baz=12