import type {SearchProvider, SearchHit, SearchOptions} from '../types';
import {fetchJson, requireKey} from './http';

interface JinaSearchResult {
  title?: string;
  url?: string;
  content?: string;
}

interface JinaSearchResponse {
  data?: JinaSearchResult[];
}

export class JinaProvider implements SearchProvider {
  readonly id = 'jina' as const;

  constructor(private getKey: () => string) {}

  async search(query: string, opts: SearchOptions): Promise<SearchHit[]> {
    const key = requireKey(this.getKey(), 'Jina');
    const data = await fetchJson<JinaSearchResponse>(
      `https://s.jina.ai/${encodeURIComponent(query)}`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${key}`,
          Accept: 'application/json',
          'X-Retain-Images': 'none',
        },
      },
    );
    return (data.data ?? []).slice(0, opts.maxResults).map(r => ({
      title: r.title ?? '',
      url: r.url ?? '',
      snippet: r.content ?? '',
    }));
  }
}
