import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author bvsr
 */
public class MultiThreadWordCount
{
    private static final int THREAD_COUNT = 4;


    private static class FileIterator implements Iterator, AutoCloseable
    {
        private final BufferedReader br;
        private String nextLine;


        public FileIterator( String fileName ) throws IOException
        {
            br = new BufferedReader( new FileReader( fileName ) );
            nextLine = br.readLine();
        }


        @Override public boolean hasNext()
        {
            return nextLine != null;
        }


        @Override public String next()
        {
            String lineToReturn = nextLine;
            try {
                nextLine = br.readLine();
            } catch ( IOException e ) {
                nextLine = null;
            }
            return lineToReturn;
        }


        @Override public void remove()
        {
            throw new NotImplementedException();
        }


        @Override public void close() throws IOException
        {
            br.close();
        }
    }


    private static class Transformers
    {
        public String[] mapToTokens( String input )
        {
            return input.split( "[ _\\.,\\-\\+]" );
        }


        private String[] filterIllegalTokens( String[] words )
        {
            List<String> filteredList = new ArrayList<>();
            for ( String word : words ) {
                if ( word.matches( "[a-zA-Z]+" ) ) {
                    filteredList.add( word );
                }
            }
            return filteredList.toArray( new String[filteredList.size()] );
        }


        private String[] mapToLowerCase( String[] words )
        {
            String[] filteredList = new String[words.length];
            for ( int i = 0; i < words.length; i++ ) {
                filteredList[i] = words[i].toLowerCase();
            }
            return filteredList;
        }


        public synchronized void reduce( Map<String, Integer> counter, String word )
        {
            if ( counter.containsKey( word ) ) {
                counter.put( word, counter.get( word ) + 1 );
            } else {
                counter.put( word, 1 );
            }
        }
    }


    private static class TransformationThread implements Runnable
    {
        private Transformers tr;
        private Queue<String> dataQueue;
        private Map<String, Integer> counters;


        public TransformationThread( Transformers tr, Map<String, Integer> counters, Queue<String> dataQueue )
        {
            this.tr = tr;
            this.dataQueue = dataQueue;
            this.counters = counters;
        }


        @Override public void run()
        {
            while ( !dataQueue.isEmpty() ) {
                String line = dataQueue.poll();
                if ( line != null ) {
                    String[] words = tr.mapToTokens( line );
                    String[] legalWords = tr.filterIllegalTokens( words );
                    String[] lowerCaseWords = tr.mapToLowerCase( legalWords );
                    for ( String word : lowerCaseWords ) {
                        tr.reduce( counters, word );
                    }
                }
            }
        }
    }


    public static void main( final String[] args ) throws Exception
    {
        Transformers tr = new Transformers();
        Map<String, Integer> counters = new HashMap<>();
        final Queue<String> dataQueue = new ConcurrentLinkedQueue<>();
        new Thread()
        {
            @Override public void run()
            {
                try ( FileIterator fc = new FileIterator( args[0] ) ) {
                    while ( fc.hasNext() ) {
                        dataQueue.add( fc.next() );
                    }
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }.start();
        while ( dataQueue.isEmpty() ) {
            // Wait for the thread to start writing into the queue
            Thread.sleep( 10 );
        }
        ExecutorService es = Executors.newFixedThreadPool( THREAD_COUNT );
        for ( int i = 0; i < THREAD_COUNT; i++ ) {
            es.execute( new TransformationThread( tr, counters, dataQueue ) );
        }
        es.shutdown();
        es.awaitTermination( 1, TimeUnit.MINUTES );

        // Sorting the Map
        List<Map.Entry<String,Integer>> sortedEntries = new ArrayList<Map.Entry<String,Integer>>(counters.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<String,Integer>>() {
                    @Override
                    public int compare(Map.Entry<String,Integer> e1, Map.Entry<String,Integer> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        System.out.println( "Word Count:\n" + sortedEntries);
    }
}