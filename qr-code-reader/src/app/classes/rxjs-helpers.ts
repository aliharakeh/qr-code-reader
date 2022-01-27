import {EMPTY, from} from 'rxjs';
import {finalize, take} from 'rxjs/operators';

function toObservable(action) {
  if (!action) {
    return EMPTY;
  }
  return from(action).pipe(
    take(1)
    // catchError(_ => of(null))
  );
}

function executeSQLWithLog(db, type, query, data = []) {
  if (!db) {
    return EMPTY;
  }
  console.log({ type, query, data });
  return toObservable(db.executeSql(query, data)).pipe(
    finalize(() => console.log(`[${type}] : Done!`))
  );
}

export {
  toObservable,
  executeSQLWithLog
};
