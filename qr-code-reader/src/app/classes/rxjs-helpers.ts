import {from} from 'rxjs';
import {finalize, take} from 'rxjs/operators';

function toObservable(action) {
  return from(action).pipe(
    take(1),
    // catchError(_ => of(null))
  );
}

function executeSQLWithLog(db, type, query, data = []) {
  console.log({ type, query, data });
  return toObservable(db.executeSql(query, data)).pipe(
    finalize(() => console.log(`[${type}] : Done!`))
  );
}

export {
  toObservable,
  executeSQLWithLog
}
