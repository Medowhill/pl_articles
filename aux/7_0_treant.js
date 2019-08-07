const config = {
  container: "#tree-ae-0",
  connectors: {
    type: 'straight'
  }
};
const n0 = {text: { name: "\\(Add\\)" }};
const n1 = {parent: n0,text: { name: "\\(Num\\)" }};
const n2 = {parent: n1,text: { name: "\\(4\\)" }};
const n3 = {parent: n0,text: { name: "\\(Sub\\)" }};
const n4 = {parent: n3,text: { name: "\\(Num\\)" }};
const n5 = {parent: n4,text: { name: "\\(2\\)" }};
const n6 = {parent: n3,text: { name: "\\(Num\\)" }};
const n7 = {parent: n6,text: { name: "\\(1\\)" }};
const simple_chart_config = [
  config, n0, n1, n2, n3, n4, n5, n6, n7
];
