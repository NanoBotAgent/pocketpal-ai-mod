import type {SearchProvider, SearchHit, SearchOptions} from '../types';
import {fetchJson, requireKey} from './http';

interface LangSearchResult {
  title?: string;
  url?: string;
  snippet?: string;
}

interface LangSearchResponse {
  results?: LangSearchResult[];
}

export class LangSearchProvider implements SearchProvider {
  readonly id = 'langsearch' as const;

  constructor(private getKey: () => string) {}

  async search(query: string, opts: SearchOptions): Promise<SearchHit[]> {
    const key = requireKey(this.getKey(), 'LangSearch');
    const data = await fetchJson<LangSearchResponse>(
      'https://api.langsearch.com/v1/web-search',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${key}`,
        },
        body: JSON.stringify({
          query,
          count: opts.maxResults,
        }),
      },
    );
    return (data.results ?? []).map(r => ({
      title: r.title ?? '',
      url: r.url ?? '',
      snippet: r.snippet ?? '',
    }));
  }
}
