const {of} = require('rxjs');
const {filter, tap} = require('rxjs/operators');

of(false).pipe(
  tap(d => console.log('before filter 1')),
  filter(data => data),
  tap(d => console.log('after filter 1')),
).subscribe(next => console.log('next 1'), err => console.log('error 1'), () => console.log('complete 1'));

console.log('---------------------------------------------')

of(true).pipe(
  tap(d => console.log('before filter 2')),
  filter(data => data),
  tap(d => console.log('after filter 2')),
).subscribe(next => console.log('next 2'), err => console.log('error 2'), () => console.log('complete 2'));
