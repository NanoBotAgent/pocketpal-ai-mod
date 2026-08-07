import type {SearchProvider, SearchHit, SearchOptions} from '../types';
import {fetchJson, requireKey} from './http';

interface FirecrawlResult {
  title?: string;
  url?: string;
  description?: string;
}

interface FirecrawlResponse {
  data?: FirecrawlResult[];
}

export class FirecrawlProvider implements SearchProvider {
  readonly id = 'firecrawl' as const;

  constructor(private getKey: () => string) {}

  async search(query: string, opts: SearchOptions): Promise<SearchHit[]> {
    const key = requireKey(this.getKey(), 'Firecrawl');
    const data = await fetchJson<FirecrawlResponse>(
      'https://api.firecrawl.dev/v1/search',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${key}`,
        },
        body: JSON.stringify({
          query,
          limit: opts.maxResults,
        }),
      },
    );
    return (data.data ?? []).map(r => ({
      title: r.title ?? '',
      url: r.url ?? '',
      snippet: r.description ?? '',
    }));
  }
}
